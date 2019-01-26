/*
 * Copyright (c) 2018. Robert Wittek <oe1rxw@gmail.com>
 *
 * This software may be modified and distributed under the terms of the MIT license.  See the LICENSE file for details.
 */

package at.wrk.coceso.alarm.text.sender.tetra;

import java.io.Serializable;

public class SendSdsRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String calledParty;
    private final String messageContent;
    private final OutgoingSdsType type;

    public SendSdsRequest(
            final String calledParty,
            final String messageContent,
            final OutgoingSdsType type) {
        this.calledParty = calledParty;
        this.messageContent = messageContent;
        this.type = type;
    }

    public String getCalledParty() {
        return calledParty;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public OutgoingSdsType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "SendSdsRequest{" +
                "calledParty='" + calledParty + '\'' +
                ", messageContent='" + messageContent + '\'' +
                ", type=" + type +
                '}';
    }
}
