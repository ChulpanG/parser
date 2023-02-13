# Web-service for parsing html pages

## Request example

```
curl --location --request GET 'http://localhost:8080/html?url=https://www.sitejabber.com/reviews/keh.com'
```
## Response example
```
{
    "reviewsCount": 70101,
    "name": "KEH",
    "rating": 4.8,
    "url": "www.keh.com"
}
```

### Technologies

* Spring WebFlux
* [Jsoup](https://jsoup.org/)
* Lombok
* WireMock, Java Faker



