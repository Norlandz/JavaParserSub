package com.redfrog.javaParserSub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.javaparser.ParseException;
import com.redfrog.javaParserSub.service.JavaCommentService;

@Controller
@RequestMapping("/v0.1")
@CrossOrigin(origins = JavaCommentController.url_JsExpress_CrossOrigin) // https://spring.io/guides/gs/rest-service-cors/
public class JavaCommentController {

  //  @Config
  public static final String url_JsExpress_CrossOrigin = "http://localhost:3000"; // @need_check uses docker is this still localhost?.

  @Autowired
  private JavaCommentService javaCommentService;

  // []
  // if you want to get RequestHeader you can simply use @RequestHeader annotation in method
  // 
  // public String getResponse(@RequestBody SomeObject object,
  //  @RequestHeader("Content-type") String contentType) {
  // <>
  // https://stackoverflow.com/questions/51754374/how-to-get-all-incoming-request-details-in-spring-rest-service
  // 
  // 
  // []
  //     public ResponseEntity<List<MyDTO>> query(HttpServletRequest request, HttpServletResponse response) {
  // <>
  // https://stackoverflow.com/questions/63958346/accessing-path-variables-and-request-parameters-without-annotation

  @PostMapping("/removeCodeCommentGivenFileCode")
  public ResponseEntity<String> removeCodeCommentGivenFileCode(@RequestBody String fileCode_ori) {
    try {
      String fileCode_CommentRemoved = JavaCommentService.removeCodeComment_givenFileCode(fileCode_ori);
      return ResponseEntity.status(HttpStatus.OK).body(fileCode_CommentRemoved);
    } catch (ParseException e) {
      // dk what if just throw in controller 
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.toString());
    }
  }

}
