spring-cloud-resttemplate-feign

[![Build Status](https://travis-ci.org/mybatis/spring.svg?branch=master)](https://travis-ci.org/mybatis/spring)
[![Coverage Status](https://coveralls.io/repos/mybatis/spring/badge.svg?branch=master&service=github)](https://coveralls.io/github/mybatis/spring?branch=master)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

This project is a easy way to using restful api by Spring's RestTemplate, just like spring-cloud-openfeign.
It's using SpringMVC annotation by deafult.

Welcome some excellecnt gays join in to improve the functions.

### Useing Example
```maven
	<dependency>
		<groupId>com.cht</groupId>
		<artifactId>spring-cloud-resttemplate-feign</artifactId>
		<version>1.0.3-RELEASE</version>
	</dependency>
```

```java
@SpringBootApplication
@EnableChtFeignClients
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
```
```java
@ChtFeignClient(value = "xxx-gateway-api", url = "http://localhost:8081")
public interface ChtTestFeignClient {

    @PostMapping(value = "/{bbb}/ttt1")
    Response<PageResult<RouteBlackDto>> ttt1(@RequestHeader("token") String token,
                                             @RequestBody Demo2Dto demoDto,
                                             @RequestParam("aaa") String aaa,
                                             @PathVariable("bbb") String bbb);
}
```
```java
@RestController
public class DemoController {

    @Resource
    private ChtTestFeignClient chtTestFeignClient;

    @GetMapping(value = "/ttt1")
    public Response<PageResult<RouteBlackDto>> ttt1() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        DemoChildDto demoChildDto = new DemoChildDto();
        demoChildDto.setAge(11);
        Demo2Dto demoDto = new Demo2Dto();
        demoDto.setName("haha");
        demoDto.setBlackTime(dateFormat.parse("2019-11-12 11:12:11"));
        demoDto.setAgeDto(demoChildDto);
        return chtTestFeignClient.ttt1("token", demoDto, "xxx", "yyy");

    }
}
```
