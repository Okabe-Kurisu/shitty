package project.Controller;

import project.Service.DemoService;
import project.Service.NotDemoService;
import webby.bean.annotation.Autowired;
import webby.utils.Result;
import webby.web.annotation.*;
import webby.web.http.HttpResponseUtil;

/**
 * program: webby
 * description:
 * author: Makise
 * create: 2019-04-17 16:10
 **/
@Controller("/demo")
public class DemoController {
    @Autowired
    private DemoService demoService;
    @Autowired
    private NotDemoService notDemoService;

    @Get("ioc")
    public Result ioc() {
        return Result.success(demoService.hello());
    }
    @Get("not/ioc")
    public Result notIoc() {
        return Result.success(notDemoService.hello());
    }


    @Post("{name}/{count}")
    public Result helloPost(@Param("name") String name, @Param("count") int count, @Param("token") String token) {
        StringBuilder hello = new StringBuilder("hello ").append(name);
        for (; count > 0; count--) {
            hello.append(" again,");
        }
        hello.append(" token is ").append(token);
        return Result.success(hello.toString());
    }

    @Delete("{name}/{count}")
    public Result helloDelete(@Param("name") String name, @Param("count") int count, @Param("token") String token) {
        return helloPost(name, count, token);
    }

    @Get("{name}/{count}")
    public Result helloGet(@Param("name") String name, @Param("count") int count, @Param("token") String token) {
        return helloPost(name, count, token);
    }

    @Put("{name}/{count}")
    public Result helloPut(@Param("name") String name, @Param("count") int count, @Param("token") String token) {
        return helloPost(name, count, token);
    }

    @Get("pressure/{name}")
    public Result pressure(@Param("name") String name, @Param("token") String token) {
        return Result.success("helloPost" + name + token);
    }

    @Get("throw")
    public void throwTest() {
        throw new RuntimeException();
    }

    @Get("download/{filename}")
    public HttpResponseUtil helloPost(@Param("filename") String filename) {
        return new HttpResponseUtil().putFile("data/" + filename);
    }
}
