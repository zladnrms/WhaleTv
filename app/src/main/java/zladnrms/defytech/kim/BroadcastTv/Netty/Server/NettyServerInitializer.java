package zladnrms.defytech.kim.BroadcastTv.Netty.Server;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    /*
     * NettyServerInitializer의 initChannel은 유저가 접속 시 마다 호출되므로
     * 항상 새로운 Handler을 호출해줘야한다 (New NettyServerHandler)
     */

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new ObjectDecoder( 1024 * 1024, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())),new ObjectEncoder());
        pipeline.addLast("handler", new NettyServerHandler());

        System.out.println("WhaleTv:"+ch.remoteAddress() +"접속");
    }
}