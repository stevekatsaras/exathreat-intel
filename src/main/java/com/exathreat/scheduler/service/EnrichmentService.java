package com.exathreat.scheduler.service;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.exathreat.common.processor.IPProcessor;
import com.maxmind.db.Network;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.AsnResponse;
import com.maxmind.geoip2.model.CityResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EnrichmentService {

	@Autowired
	private DatabaseReader geoLite2CityReader;

	@Autowired
	private DatabaseReader geoLite2ASNReader;

	@Autowired
	private IPProcessor ipProcessor;

	@ServiceActivator(inputChannel = "enrichmentInChannel", outputChannel = "indexInChannel")
	public Message<List<Map<String, Object>>> enrich(Message<List<Map<String, Object>>> msg) {
		List<Map<String, Object>> iocList = msg.getPayload();
		for (Map<String, Object> iocMap : iocList) {
			String ioc = (String) iocMap.get("ioc");
			String type = (String) iocMap.get("type");
			
			if ("ipv4".equals(type)) {
				ioc = ipProcessor.extract(ioc);
				try {
					iocMap.putAll(geo(ioc));
				}
				catch (Exception exception) {
					log.error("[EnrichmentService.enrich] - failed to enrich " + iocMap.get("ioc") + ". exception: " + exception.getMessage() + ".", exception);
				}
			}

			iocMap.put("ioc", ioc);
			iocMap.put(type, ioc);
		}
		return new GenericMessage<List<Map<String,Object>>>(iocList);
	}

	private Map<String, Object> geo(String ipAddress) throws Exception {
		Map<String, Object> geo = new HashMap<String, Object>();

		Optional<AsnResponse> optionalAsnResponse = geoASNLocation(ipAddress);
		if (optionalAsnResponse.isPresent()) {
			AsnResponse asnResponse = optionalAsnResponse.get();
			if (asnResponse.getAutonomousSystemNumber() != null && asnResponse.getAutonomousSystemOrganization() != null) {
				Network network = asnResponse.getNetwork();
				geo.put("geo_asn.asn", asnResponse.getAutonomousSystemNumber());
				geo.put("geo_asn.organization", asnResponse.getAutonomousSystemOrganization());
				geo.put("geo_asn.cidr", network.getNetworkAddress().getHostAddress() + "/" + network.getPrefixLength());
			}
		}
		
		Optional<CityResponse> optionalCityResponse = geoIPLocation(ipAddress);
		if (optionalCityResponse.isPresent()) {
			CityResponse cityResponse = optionalCityResponse.get();
			if (cityResponse.getCity().getName() != null) {
				geo.put("geo_city.name", cityResponse.getCity().getName());
			}
			if (cityResponse.getPostal().getCode() != null) {
				geo.put("geo_city.postcode", cityResponse.getPostal().getCode());
			}
			geo.put("geo_city.continent.code", cityResponse.getContinent().getCode());
			geo.put("geo_city.continent.name", cityResponse.getContinent().getName());
			geo.put("geo_city.country.code", cityResponse.getCountry().getIsoCode());
			geo.put("geo_city.country.name", cityResponse.getCountry().getName());
			geo.put("geo_city.location.lat", cityResponse.getLocation().getLatitude());
			geo.put("geo_city.location.lon", cityResponse.getLocation().getLongitude());
			geo.put("geo_city.location.radius", cityResponse.getLocation().getAccuracyRadius());
			geo.put("geo_city.location.timezone", cityResponse.getLocation().getTimeZone());
		}
		return geo;
	}

	private Optional<AsnResponse> geoASNLocation(String ipAddress) throws Exception {
		return geoLite2ASNReader.tryAsn(InetAddress.getByName(ipAddress));
	}

	private Optional<CityResponse> geoIPLocation(String ipAddress) throws Exception {
		return geoLite2CityReader.tryCity(InetAddress.getByName(ipAddress));
	}
}