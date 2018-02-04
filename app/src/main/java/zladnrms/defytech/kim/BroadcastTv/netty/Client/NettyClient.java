package zladnrms.defytech.kim.BroadcastTv.netty.Client;

import android.content.Context;

import com.orhanobut.logger.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import zladnrms.defytech.kim.BroadcastTv.BuildConfig;
import zladnrms.defytech.kim.BroadcastTv.packet.ChangeSubjectPacket;
import zladnrms.defytech.kim.BroadcastTv.packet.ChatPacket;
import zladnrms.defytech.kim.BroadcastTv.packet.ConnectPacket;
import zladnrms.defytech.kim.BroadcastTv.packet.EndingPacket;
import zladnrms.defytech.kim.BroadcastTv.packet.EntryPacket;
import zladnrms.defytech.kim.BroadcastTv.packet.HeaderPacket;
import zladnrms.defytech.kim.BroadcastTv.packet.StopPacket;

public class NettyClient extends Thread {

    private String host = BuildConfig.TCP_URL;
    private int port = 6060;
    private boolean submitFlag = false;
    private int type;

    private HeaderPacket headerPacket;
    private EntryPacket entryPacket;
    private ChatPacket chatPacket;
    private ConnectPacket connectPacket;
    private EndingPacket endingPacket;
    private ChangeSubjectPacket csePacket;
    private StopPacket stopPacket;

    private Context context; // 추가

    private boolean flag = true;

    private static NettyClient instance = new NettyClient();

    public NettyClient() {

    }

    public static NettyClient getInstance() {
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new NettyClientInitializer(context));
            Channel channel = bootstrap.connect(host, port).sync().channel();

            while (flag) {
                if(submitFlag) {
                    switch (this.type) {
                        case 0:
                            channel.writeAndFlush(this.entryPacket);
                            break;
                        case 1:
                            channel.writeAndFlush(this.entryPacket);
                            break;
                        case 2:
                            channel.writeAndFlush(this.chatPacket);
                            break;
                        case 10:
                            channel.writeAndFlush(this.csePacket);
                            break;
                        case 50:
                            channel.writeAndFlush(this.stopPacket);
                            break;
                        case 51:
                            channel.writeAndFlush(this.stopPacket);
                            break;
                        case 100:
                            channel.writeAndFlush(this.connectPacket);
                            break;
                        case 101:
                            channel.writeAndFlush(this.endingPacket);
                            break;
                    }
                    submitFlag = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public void send(int type, HeaderPacket headerPacket) {
        switch (type) {
            case 0:
                this.type = 0;
                this.entryPacket = (EntryPacket) headerPacket;
                submitFlag = true;
                break;
            case 1:
                this.type = 1;
                this.entryPacket = (EntryPacket) headerPacket;
                submitFlag = true;
                break;
            case 2:
                this.type = 2;
                this.chatPacket = (ChatPacket) headerPacket;
                submitFlag = true;
                break;
            case 10:
                this.type = 10;
                this.csePacket = (ChangeSubjectPacket) headerPacket;
                submitFlag = true;
                break;
            case 50:
                this.type = 50;
                this.stopPacket = (StopPacket) headerPacket;
                submitFlag = true;
                break;
            case 51:
                this.type = 51;
                this.stopPacket = (StopPacket) headerPacket;
                submitFlag = true;
                break;
            case 100:
                this.type = 100;
                this.connectPacket = (ConnectPacket) headerPacket;
                submitFlag = true;
                break;
            case 101:
                this.type = 101;
                this.endingPacket = (EndingPacket) headerPacket;
                submitFlag = true;
                break;
        }
    }
}