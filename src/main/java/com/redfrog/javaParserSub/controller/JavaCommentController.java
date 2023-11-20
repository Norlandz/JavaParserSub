package com.redfrog.javaParserSub.controller;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.javaparser.ParseException;
import com.redfrog.javaParserSub.msgSchema.CodeConversionInputMsg;
import com.redfrog.javaParserSub.msgSchema.CodeConversionOutputMsg;
import com.redfrog.javaParserSub.service.JavaCommentService;

@Controller
@RequestMapping("/v0.1")
@CrossOrigin(origins = JavaCommentController.url_JsExpress_CrossOrigin) // https://spring.io/guides/gs/rest-service-cors/
public class JavaCommentController {

  //  @Config
  public static final String url_JsExpress_CrossOrigin = "http://localhost:3000"; // @need_check uses docker is this still localhost?. // cors is just for browser... now its indirectly accessed in backend, guess no need // for docker, see "network_mode" 

  @Autowired
  private JavaCommentService javaCommentService;

  //  // []
  //  // if you want to get RequestHeader you can simply use @RequestHeader annotation in method
  //  // 
  //  // public String getResponse(@RequestBody SomeObject object,
  //  //  @RequestHeader("Content-type") String contentType) {
  //  // <>
  //  // https://stackoverflow.com/questions/51754374/how-to-get-all-incoming-request-details-in-spring-rest-service
  //  // 
  //  // 
  //  // []
  //  //     public ResponseEntity<List<MyDTO>> query(HttpServletRequest request, HttpServletResponse response) {
  //  // <>
  //  // https://stackoverflow.com/questions/63958346/accessing-path-variables-and-request-parameters-without-annotation
  //
  //  // []
  //  //     @RequestMapping(value = "so", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  //  // <>
  //  // https://stackoverflow.com/questions/18385361/return-json-for-responseentitystring
  //  // 
  //  // []
  //  // And as you have annotated with `@RestController` there is no need to do explicit json conversion. Just return a POJO and jackson serializer will take care of converting to json.
  //  // <>
  //  // https://stackoverflow.com/questions/44839753/returning-json-object-as-response-in-spring-boot
  //  // 
  //  // []
  //  // Now what the annotation means is that the returned value of the method will constitute the body of the HTTP response. Of course, an HTTP response can't contain Java objects. So this list of accounts is transformed to a format suitable for REST applications, typically JSON or XML.
  //  // 
  //  // The choice of the format depends on the installed message converters, on the values of the [`produces` attribute](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/bind/annotation/RequestMapping.html#produces--) of the `@RequestMapping` annotation,
  //  // <>
  //  // https://stackoverflow.com/questions/28646332/how-does-the-spring-responsebody-annotation-work
  //  // 
  //  // []
  //  // `@GetMapping(value = "/myapi/{id}", produces = MediaType.APPLICATION_JSON_VALUE)`
  //  // <>
  //  // https://stackoverflow.com/questions/46197215/getmapping-not-produces-response-content-type-application-json
  //
  //  // java - What is the difference between ResponseEntity<T> and @ResponseBody? - Stack Overflow
  //  // https://stackoverflow.com/questions/22725143/what-is-the-difference-between-responseentityt-and-responsebody
  //  // 
  //  // java - How to respond with an HTTP 400 error in a Spring MVC @ResponseBody method returning String - Stack Overflow
  //  // https://stackoverflow.com/questions/16232833/how-to-respond-with-an-http-400-error-in-a-spring-mvc-responsebody-method-retur
  //  
  //  // java - @RequestBody usage with multiple arguments - Stack Overflow
  //  // https://stackoverflow.com/questions/60822381/requestbody-usage-with-multiple-arguments
  //  // 
  //  // java - Passing multiple variables in @RequestBody to a Spring MVC controller using Ajax - Stack Overflow
  //  // https://stackoverflow.com/questions/12893566/passing-multiple-variables-in-requestbody-to-a-spring-mvc-controller-using-ajax
  //
  //  // java - Find the Content-type of the incoming request in Spring boot - Stack Overflow
  //  // https://stackoverflow.com/questions/68800947/find-the-content-type-of-the-incoming-request-in-spring-boot
  //  // 
  //  // @RequestHeader :: Spring Framework
  //  // https://docs.spring.io/spring-framework/reference/web/webflux/controller/ann-methods/requestheader.html
  //  
  @PostMapping(value = "/removeCodeCommentGivenFileCode", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CodeConversionOutputMsg> removeCodeCommentGivenFileCode(@RequestBody CodeConversionInputMsg codeConversionInputMsg,
                                                                                @RequestHeader("Content-type") String contentType) {
    if (!contentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
      return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(null);
      // @need_check: should Spring already block that in the Annotation specify... 
      // @think: also is null a json type, rt error handling pb, should i return that..
      // header case insensitive thing also, dk Java
    }
    try {
      String fileCode_CommentRemoved = JavaCommentService.removeCodeComment_givenFileCode(codeConversionInputMsg.codeInput);
      return ResponseEntity.status(HttpStatus.OK).body(new CodeConversionOutputMsg(fileCode_CommentRemoved, null, null));
    } catch (ParseException e) {
      // dk what if just throw in controller 
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CodeConversionOutputMsg(null, e.getClass().getName(), e.toString()));
    }
  }

}
