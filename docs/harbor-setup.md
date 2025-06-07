# Harbor HTTPS Setup with Self-Signed Certificate (SAN Included)
This document provides a complete guide to configuring Harbor with HTTPS using a self-signed certificate  
and enabling Docker to trust that certificate for secure image push and pull operations.

## 1. Create Openssl with SAN (openssl-san.cnf)
```ini
[req]
default_bits       = 2048
distinguished_name = req_distinguished_name
req_extensions     = req_ext
x509_extensions    = v3_req
prompt             = no

[req_distinguished_name]
C  = KR
ST = Seoul
L  = Seoul
O  = MyCompany
OU = MyOrg
CN = [myip]

[req_ext]
subjectAltName = @alt_names

[v3_req]
keyUsage = keyEncipherment, dataEncipherment, digitalSignature
extendedKeyUsage = serverAuth
subjectAltName = @alt_names

[alt_names]
IP.1 = [myip]
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
> Check harbor.yml : localhost

## 5. Register the Certificate with Docker
```
mkdir -p ~/.docker/certs.d/[myip]
cp server.crt ~/.docker/certs.d/[myip]/ca.crt
```

## 6. Restart Docker
Restart Docker Desktop from the system tray.

## 7. Docker Login
```
docker login https://[myip]
```
> check ip

## 8. Push Docker Image
docker tag traffic-app:v1 [myip]/istio-lab/traffic-app:v1
docker push [myip]/istio-lab/traffic-app:v1

## 9. Create k3d Cluster with Harbor Certificate (When IP Changes)
If your local IP changes and you regenerate your Harbor certificate, you also need to mount the new certificate into the cluster:

```
k3d cluster create istio-lab \
  --api-port 6550 \
  -p "30100:8080@loadbalancer" \
  -p "8443:443@loadbalancer" \
  --volume $(pwd)/custom-ca.crt:/etc/ssl/certs/ca-certificates.crt \
  --k3s-arg "--disable=traefik@server:0" \
  --network k3d-istio-lab \
  --k3s-arg "--cluster-cidr=10.42.0.0/16@server:0" \
  --k3s-arg "--service-cidr=10.43.0.0/16@server:0" \
  --k3s-arg "--node-ip=172.19.0.4@server:0" \
  --k3s-arg "--flannel-backend=host-gw@server:0" \
  --verbose
```
> Make sure port 8081 and 8444 do not conflict with other services.
This mounts the updated certificate into the container so it can verify Harbor’s TLS.

## 10. Restart Harbor
check process 4

## 11. Apply Kubernetes Deployment
(opt) You may need to recreate the namespace if it was deleted:
```
kubectl create namespace dev-lab
```
> check your namespace

Apply
```
kubectl apply -f deployment-v1.yaml
kubectl apply -f deployment-v2.yaml
```

## 12. Verify Deployment
```
kubectl get pods -n dev-lab -w
```
> Pods should eventually show STATUS: Running.

•	If your IP address changes, you must regenerate the certificate with the new IP in SAN.
