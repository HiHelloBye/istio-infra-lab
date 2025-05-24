# Harbor HTTPS Setup with Self-Signed Certificate (SAN Included)
This document provides a complete guide to configuring Harbor with HTTPS using a self-signed certificate  
and enabling Docker to trust that certificate for secure image push and pull operations.

## 1. Create Openssl with SAN (openssl-san.cnf)
```ini
[req]
distinguished_name = req_distinguished_name
x509_extensions = v3_req
prompt = no

[req_distinguished_name]
C = KR
ST = Seoul
L = Seoul
O = MyCompany
OU = MyOrg
CN = 192.168.1.39

[v3_req]
keyUsage = keyEncipherment, dataEncipherment
extendedKeyUsage = serverAuth
subjectAltName = @alt_names

[alt_names]
IP.1 = 192.168.1.39
```

> Replace the IP address with the one you use for Harbor (e.g., your local or fixed IP).

## 2. Generate the Certificate
```
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout server.key -out server.crt \
  -config openssl-san.cnf
```

## 3. Copy Certificates to Harbor Directory
```
cp server.crt /Users/hello/certs/server.crt
cp server.key /Users/hello/certs/server.key
```
> These paths must match your harbor.yml configuration.

## 4. Reconfigure and Restart Harbor
```
cd ~/harbor
./prepare
docker-compose down -v
docker-compose up -d
```

## 5. Register the Certificate with Docker
```
mkdir -p ~/.docker/certs.d/192.168.1.39
cp server.crt ~/.docker/certs.d/192.168.1.39/ca.crt
```

## 6. Restart Docker
Restart Docker Desktop from the system tray.

## 7. Docker Login
```
docker login https://192.168.1.39
```
> check ip

## 8. Push Docker Image
docker tag traffic-app:v1 192.168.1.39/istio-lab/traffic-app:v1
docker push 192.168.1.39/istio-lab/traffic-app:v1


•	If your IP address changes, you must regenerate the certificate with the new IP in SAN.
