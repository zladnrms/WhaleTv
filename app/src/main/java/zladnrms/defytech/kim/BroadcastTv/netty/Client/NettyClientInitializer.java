package zladnrms.defytech.kim.BroadcastTv.netty.Client;

import android.content.Context;

import com.orhanobut.logger.Logger;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    private Context context;

    public NettyClientInitializer(Context context) {
        this.context = context;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();

        Logger.t("initChannel").d(ch.pipeline().channel() + " channel 초기화");

        pipeline.addLast(new ObjectDecoder( 1024 * 1024, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())),new ObjectEncoder());
        pipeline.addLast("handler", new NettyClientHandler(context));
    }
}