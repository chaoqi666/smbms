<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="com.qi.util.Constants" %>
<%@ page import="com.qi.pojo.User" %>
<%@include file="/jsp/common/head.jsp"%>

<%
    User user = (User) request.getSession().getAttribute(Constants.USER_SESSION);
%>
<div class="right">
    <img class="wColck" src="../images/clock.jpg" alt=""/>
    <div class="wFont">
        <h2><%=user.getUserName()%></h2>
        <p>欢迎来到超市订单管理系统!</p>
    </div>
</div>
</section>
<%@include file="/jsp/common/foot.jsp" %>
