# moex-stock-api
Prerequisites `kubectl`, `minikube`, `oracle virtual box`<br/>
1. `minikube start`<br/>
2. `kubectl apply -f https://github.com/Dankosik/moex-stock-api/kubernetes/moex-stock-api.yaml`<br/>
3. `kubectl apply -f https://github.com/Dankosik/moex-stock-api/kubernetes/ingress.yaml`<br/>
4. `kubectl apply -f https://github.com/Dankosik/moex-stock-api/kubernetes/role.yaml`<br/>
5. `kubectl apply -f https://github.com/Dankosik/stock-api/kubernetes/stock-api.yaml`<br/>
6. `kubectl apply -f https://github.com/Dankosik/yahoo-stock-api/kubernetes/yahoo-stock-api.yaml`<br/>
