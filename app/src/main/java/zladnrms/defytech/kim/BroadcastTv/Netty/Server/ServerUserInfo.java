package zladnrms.defytech.kim.BroadcastTv.Netty.Server;

import java.io.Serializable;

import io.netty.channel.Channel;

/**
 * Created by kim on 2017-04-05.
 */

public class ServerUserInfo implements Serializable {

    private static final long serialVersionUID = 10000L;

    private int roomId;
    private String nickname;
    private int position;
    private Channel channel;

    public ServerUserInfo(int roomId, String nickname, Channel channel){
        this.roomId = roomId;
        this.nickname = nickname;
        this.channel = channel;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
