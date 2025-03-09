exathreat-intel
---------------
A SpringBoot Scheduler that periodically fetches SEIM intelligence and stores it into an ElasticSearch instance. This intelligence is helpful in enriching a Clients ingested events from their network devices. Some of the SEIM intelligence fetched may be: bad/blacklisted IP addresses, bad domains, risk scores, malicious URLs, etc.

Some of the feeds used are:

1. MAXMIND GeoIP2
2. AlienVault Reputation
3. AlphaSOC Ryuk C2
4. Botvrij.eu
5. Brute Force Blocker
6. Darklist
7. ProofPoint Compromised IPs
8. Feodo Tracker - IP Blocklist
9. Green Snow - Blocklist
10. Linux Tracker - Blocklists
11. InfoSec Cert
12. Joe Wein
13. NoCoin
14. OpenPhish
15. VX Vault
16. Botnet C2
17. Blocklist.de
18. Binary Defense
19. BitNodes
20. Blackbook
21. BotScout
22. CINS Army List
23. Cobalt Strike Server
24. Cruzit
25. Cybercrime Tracker
26. James Brine
27. Rutgers
28. sblam.com
29. ThreatFox
30. TOR Exit Nodes
31. Viriback
32. IPSpamList
33. Mirai Tracker
34. MalSilo

This service uses MAXMIND GeoIP2 databases.
To download the latest, please visit: https://www.maxmind.com/en/accounts/522260/geoip/downloads

To build your application:
*. ./gradlew clean build

To build your docker image: 
*. docker build -t exathreat-intel .

To list your docker images:
*. docker image list | grep exathreat-intel

To run your docker image with an active spring profile:
*. docker run -e PROFILE=local -e ES_DOMAIN=192.168.1.82 -e ES_PORT=9200 -e ES_SCHEME=http -p 8082:8082 exathreat-intel

To remove your dangling images:
*. docker rmi -f $(docker images --filter dangling=true)

To authenticate docker to your AWS ECR registry:
*. aws ecr get-login-password --region ap-southeast-2 | docker login --username AWS --password-stdin 367480315855.dkr.ecr.ap-southeast-2.amazonaws.com

---

To tag your docker image to your ECR repository in AWS:
*. docker tag exathreat-intel 367480315855.dkr.ecr.ap-southeast-2.amazonaws.com/exathreat-intel

To push your tagged docker image to your ECR repository in AWS:
*. docker push 367480315855.dkr.ecr.ap-southeast-2.amazonaws.com/exathreat-intel

To pull your docker image from your ECR repository in AWS:
*. docker pull 367480315855.dkr.ecr.ap-southeast-2.amazonaws.com/exathreat-intel
