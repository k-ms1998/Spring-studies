package hello.exception.servlet;

public enum DispatcherType {
    FORWARD,
    INCLUDE,
    REQUEST,
    ASYNC,
    ERROR
    /**
     * FORWARD : MVC에서 배웠던 서블릿에서 다른 서블릿이나 JSP를 호출할 때 => RequestDispatcher.forward(request, response);
     * INCLUDE : 서블릿에서 다른 서블릿이나 JSP의 결과를 포함할 때 => RequestDispatcher.include(request, response);
     * REQUEST : 클라이언트 요청
     * ASYNC : 서블릿 비동기 호출
     * ERROR : 오류 요청
     */
}
