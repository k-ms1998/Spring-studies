package hello.core.web;

import hello.core.common.MyLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
//@RequiredArgsConstructor
public class LogDemoService {
    private final MyLogger myLogger;
//    private final ObjectProvider<MyLogger> myLoggerProvider;

    public LogDemoService(MyLogger myLogger) {
        this.myLogger = myLogger;
    }

//    public LogDemoService(ObjectProvider<MyLogger> myLogger) {
//        this.myLoggerProvider = myLogger;
//    }

    public void logic(String id) {
//        MyLogger myLogger = myLoggerProvider.getObject();
        myLogger.log("service id="+id);
    }
}
