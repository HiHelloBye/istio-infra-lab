# Traffic Split (Weighted Routing)
This directory contains Kubernetes and Istio manifests for splitting traffic between two versions of a service.

## Files
- Deployment-v1.yaml
- Deployment-v2.yaml
- Service.yaml
- VirtualService.yaml

> Note: In production, we usually group these resources into a single YAML per application.  
> Here, they are separated to clarify the purpose of each component in the context of Istio traffic routing.