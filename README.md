# Weather Open API Backend with Java Springboot

Assignment Question: 
[Code Test - Build Weather App API.pdf](https://github.com/user-attachments/files/20816011/Code.Test.-.Build.Weather.App.API.pdf)



1. Run the program with intellij IDEA or any of your favourite IDE. Run the main program. 
2. Call the api with headers as "X-API-KEY: APIKEY-001". You can call the backend with 5 different api key with APIKEY-002, APIKEY-003 and so on until APIKEY-005.
3. Paste the curl into your postman to test your rest api call.


```
curl -X GET \
     -H "X-API-KEY: APIKEY-001" \
     "http://localhost:8080/api/v1/weathers/{Country}/{city}"

```

An example output for showing the rate limiting api call for 5 times an hour and response from the api :

![image](https://github.com/user-attachments/assets/21ad24cd-b6d8-41ca-9c26-dfeea332cb04)
