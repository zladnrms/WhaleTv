package zladnrms.defytech.kim.BroadcastTv.netty.Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import zladnrms.defytech.kim.BroadcastTv.packet.ChangeSubjectPacket;
import zladnrms.defytech.kim.BroadcastTv.packet.ChatPacket;
import zladnrms.defytech.kim.BroadcastTv.packet.ConnectPacket;
import zladnrms.defytech.kim.BroadcastTv.packet.EndingPacket;
import zladnrms.defytech.kim.BroadcastTv.packet.EntryPacket;
import zladnrms.defytech.kim.BroadcastTv.packet.HeaderPacket;
import zladnrms.defytech.kim.BroadcastTv.packet.StopPacket;

public class NettyServerHandler extends SimpleChannelInboundHandler<HeaderPacket> {

    /*
     * NettyHandler는 각 클라이언트에게 한 개 씩 배정된다 (Unique)
     */

    private ServerUserInfo serverUserInfo;
    private ArrayList<ServerUserInfo> serverUserInfoArr = new ArrayList<ServerUserInfo>(); // 유저 개개인 정보

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private NettyServer ns = NettyServer.getInstance();

    public NettyServerHandler() {
        // SingleTone
        serverUserInfoArr = ns.getServerUserInfoArr();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);

        System.out.println("REGISTER");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        System.out.println("HANDLER ADDED");
        /*
         * 클라이언트 접속 시
         */
        Channel incoming = ctx.channel();

        /*
        // BroadcastJavaCvActivity a message to multiple Channels
        channels.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " handlerAdded");
        */

        channels.add(incoming);

        serverUserInfo = new ServerUserInfo(0, "", incoming);
        serverUserInfoArr.add(serverUserInfo);

        System.out.println("Incoming : " + incoming + ", 서버 접속자 수 : " + serverUserInfoArr.size());


    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        /*
        // BroadcastJavaCvActivity a message to multiple Channels
        channels.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " handlerRemoved");

        // A closed Channel is automatically removed from ChannelGroup,
        // so there is no need to do "channels.remove(ctx.channel());"
        */
        System.out.println("HANDLER REMOVED");

        //ctx.pipeline().fireChannelUnregistered();

        /*
         * 퇴장 처리
         */
        Channel incoming = ctx.channel();

        EntryPacket entryPacket_1 = new EntryPacket(serverUserInfo.getRoomId(), serverUserInfo.getNickname(), 1);

        for (Channel channel : channels) {
            if (channel != incoming){ // 다른 사람
                channel.writeAndFlush(entryPacket_1);
            } else { // 자기 자신
                channel.writeAndFlush(entryPacket_1);
            }
        }

        toServer(serverUserInfo.getRoomId(), serverUserInfo.getNickname(), 1);

        serverUserInfoArr.remove(serverUserInfo);

        System.out.println("Incoming : " + incoming + ", 서버 접속자 수 : " + serverUserInfoArr.size());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HeaderPacket headerPacket) { // (4)
        Channel incoming = ctx.channel();

        int requestCode =  headerPacket.getRequestCode();
        System.out.println("[ IP : " + incoming.remoteAddress() + ", RequestCode : " + headerPacket.getRequestCode() + " ]");

        switch (requestCode) {
            case 0: // App에 유저 입장
                EntryPacket entryPacket_0 = (EntryPacket) headerPacket;
                String nickname_0 = entryPacket_0.getNickname();
                int roomId_0 = entryPacket_0.getRoomId();

                int indexOf_0 = serverUserInfoArr.indexOf(serverUserInfo);
                serverUserInfo.setRoomId(roomId_0);

                serverUserInfoArr.get(indexOf_0).setRoomId(roomId_0);

                System.out.println("『" + roomId_0 + "』" + nickname_0 + "유저 입장");

                for (Channel channel : channels) {
                    if (channel != incoming){ // 다른 사람
                        channel.writeAndFlush(entryPacket_0);
                    } else { // 자기 자신
                        channel.writeAndFlush(entryPacket_0);
                    }
                }

                //toServer(roomId_0, nickname_0, 0);
                break;

            case 1: // App에서 유저 퇴장
                EntryPacket entryPacket_1 = (EntryPacket) headerPacket;
                String nickname_1 = entryPacket_1.getNickname();
                int roomId_1 = entryPacket_1.getRoomId();

                int indexOf_1 = serverUserInfoArr.indexOf(serverUserInfo);
                serverUserInfo.setRoomId(-1);

                serverUserInfoArr.get(indexOf_1).setRoomId(-1);

                System.out.println("『" + roomId_1 + "』" + nickname_1 + "유저 퇴장");

                for (Channel channel : channels) {
                    if (channel != incoming){ // 다른 사람
                        channel.writeAndFlush(entryPacket_1);
                    } else { // 자기 자신
                        channel.writeAndFlush(entryPacket_1);
                    }
                }

                //toServer(roomId_1, nickname_1, 1);
                break;

            case 2: // CHAT 패킷
                ChatPacket chatPacket_2 = (ChatPacket) headerPacket;

                int roomId_2 = chatPacket_2.getRoomId();
                String nickname_2 = chatPacket_2.getNickname();
                String chat_2 = chatPacket_2.getChat();

                System.out.println("『" + roomId_2 + "』" + nickname_2 + " : " + chat_2);

                for (Channel channel : channels) {
                    if (channel != incoming){ // 다른 사람
                        channel.writeAndFlush(chatPacket_2);
                    } else { // 자기 자신
                        channel.writeAndFlush(chatPacket_2);
                    }
                }
                break;

            case 10: // 방송 종료 알림 패킷
                ChangeSubjectPacket csePacket_10 = (ChangeSubjectPacket) headerPacket;

                int roomId_10 = csePacket_10.getRoomId();
                String subject_10 = csePacket_10.getSubject();

                System.out.println("『" + roomId_10 + "』" + " : 방송 제목 변경 -> " + subject_10);

                for (Channel channel : channels) {
                    if (channel != incoming){ // 다른 사람
                        channel.writeAndFlush(csePacket_10);
                    } else { // 자기 자신
                        channel.writeAndFlush(csePacket_10);
                    }
                }
                break;

            case 50: // 방송 종료 알림 패킷
                StopPacket stopPacket_50 = (StopPacket) headerPacket;

                int roomId_50 = stopPacket_50.getRoomId();

                System.out.println("『" + roomId_50 + "』" + " : 방송 일시 중단 ");

                for (Channel channel : channels) {
                    if (channel != incoming){ // 다른 사람
                        channel.writeAndFlush(stopPacket_50);
                    } else { // 자기 자신
                        channel.writeAndFlush(stopPacket_50);
                    }
                }
                break;

            case 51: // 방송 종료 알림 패킷
                StopPacket stopPacket_51 = (StopPacket) headerPacket;

                int roomId_51 = stopPacket_51.getRoomId();

                System.out.println("『" + roomId_51 + "』" + " : 방송 일시 중단 ");

                for (Channel channel : channels) {
                    if (channel != incoming){ // 다른 사람
                        channel.writeAndFlush(stopPacket_51);
                    } else { // 자기 자신
                        channel.writeAndFlush(stopPacket_51);
                    }
                }
                break;

            case 100: // 입장, 퇴장 패킷
                ConnectPacket connectPacket_100 = (ConnectPacket) headerPacket;

                String nickname_100 = connectPacket_100.getNickname();

                int indexOf_100 = serverUserInfoArr.indexOf(serverUserInfo);
                serverUserInfo.setNickname(nickname_100);
                serverUserInfo.setRoomId(-1);

                serverUserInfoArr.get(indexOf_100).setNickname(nickname_100);
                serverUserInfoArr.get(indexOf_100).setRoomId(-1);

                System.out.println("『" + "-1" + "』" + nickname_100 + "유저 입장");

                for (Channel channel : channels) {
                    if (channel != incoming){ // 다른 사람
                        channel.writeAndFlush(connectPacket_100);
                    } else { // 자기 자신
                        channel.writeAndFlush(connectPacket_100);
                    }
                }
                break;

            case 101: // 방송 종료 알림 패킷
                EndingPacket endingPacket_101 = (EndingPacket) headerPacket;

                int roomId_101 = endingPacket_101.getRoomId();
                String nickname_101 = endingPacket_101.getNickname();

                System.out.println("『" + roomId_101 + "』" + nickname_101 + " : 의 방송이 종료되었습니다");

                for (Channel channel : channels) {
                    if (channel != incoming){ // 다른 사람
                        channel.writeAndFlush(endingPacket_101);
                    } else { // 자기 자신
                        channel.writeAndFlush(endingPacket_101);
                    }
                }
                break;
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception { // (5)
        System.out.println("CHANNEL ACTIVE");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception { // (6)
        System.out.println("CHANNEL INACTIVE");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Channel incoming = ctx.channel();
        System.out.println("SimpleChatClient:"+incoming.remoteAddress()+"ExceptionCaught");

        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);

        System.out.println("UNREGISTERED");

        ctx.pipeline().remove(this);
    }

    private void toServer(int roomId, String nickname, int requestCode) {
        try {
            URL url = null;

            switch (requestCode) {
                case 0:
                    url = new URL("http://52.79.108.8/whaleTv/room/enter_room.php");
                    break;
                case 1:
                    url = new URL("http://52.79.108.8/whaleTv/room/exit_room.php");
                    break;
            }

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if (conn != null) {
                conn.setConnectTimeout(10000);
                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setInstanceFollowRedirects(false); // 추가됨
                conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");

                StringBuffer buffer = new StringBuffer();
                if(requestCode==0 || requestCode==1){
                    buffer.append("roomId").append("=").append(roomId).append("&");
                    buffer.append("nickname").append("=").append(nickname);
                }

                OutputStreamWriter outStream = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                PrintWriter writer = new PrintWriter(outStream);
                writer.write(buffer.toString());
                writer.flush();

                // 보내기  &&  받기
                //헤더 받는 부분

                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuilder builder = new StringBuilder();
                String result;
                while ((result = reader.readLine()) != null) {       // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
                    builder.append(result + "\n");
                }

                if (builder.toString().trim().equals("success")) {

                }
            }

        } catch (final Exception e) {

            if(e.getMessage().equals("connect timed out")) {

            } else {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}