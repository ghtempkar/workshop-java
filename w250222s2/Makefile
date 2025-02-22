run1:
	curl -v -X POST http://localhost:8080/orders \
         -u admin:adminpassword \
         -H "Content-Type: application/json" \
         -d '{"description": "Zamówienie nr 1"}'

run3:
	curl -X POST http://localhost:8080/orders \
         -u admin:adminpassword \
         -H "Content-Type: application/json" \
         -d '{"description": "Zamówienie nr 1"}'

run2:
	curl -vs -X GET http://localhost:8080/orders -u user:userpassword
