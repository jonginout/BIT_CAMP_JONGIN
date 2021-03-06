<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>    
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="robots" content="noindex">
    <title>Dream Lecture</title>
    <%@ include file="baseinclude.jsp" %>
  </head>
  <body class="sidebar-collapsible sidebar-offcanvas">
    <div class="st-wrapper">
      

	<div class="st-wrapper">
      <div class="st-auth">
        <div class="st-auth__cont">
          <div class="st-panel">
            <div class="st-panel__cont">
              <div class="st-panel__header">
                <div class="fluid-cols">
                  <div class="expand-col"><span class="st-panel__title"><span>Create Account</span></span></div>
                </div>
              </div>
              <div class="st-panel__content">
                <form class="form" id="register-form" action="index.html" method="post">
                  <div class="form-group">
                    <input class="form-control" type="email" placeholder="Email" required>
                  </div>
                  <div class="form-group">
                    <input class="form-control" type="text" placeholder="Username" required>
                  </div>
                  <div class="form-group">
                    <input class="form-control" type="password" placeholder="Password" required>
                  </div>
                  <div class="form-group">
                    <div class="fluid-cols">
                      <div class="expand-col">
                        <div class="checkbox" id="register-terms">
                          <input id="terms" type="checkbox" required>
                          <label for="terms">Terms</label>
                        </div>
                      </div>
                      <div class="min-col">
                        <div class="btn-group btn-group-inline">
                          <button class="btn btn-info" type="submit">Register</button>
                          <button class="btn btn-info dropdown-toggle" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true"><span class="caret"></span></button>
                          <ul class="dropdown-menu">
                            <li><a href="app-login.html">Login</a></li>
                            <li><a href="app-password.html">Restore Password</a></li>
                          </ul>
                        </div>
                      </div>
                    </div>
                  </div>
                </form>
              </div>
            </div>
          </div>
          <div class="st-auth__back"><a class="btn btn-default" href="app-auth.html"><i class="fa fa-angle-left"></i>&nbsp;&nbsp;Back to Dashboard</a></div>
        </div>
      </div>
    </div><!-- Common Libs JS -->
    
    <%@ include file="index-js-lib.jsp" %>
    
 </body>
</html>