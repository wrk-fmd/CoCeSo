/*
 * Copyright (c) 2018. Robert Wittek <oe1rxw@gmail.com>
 *
 * This software may be modified and distributed under the terms of the MIT license.  See the LICENSE file for details.
 */

package at.wrk.coceso.alarm.text.sender.tetra;

import com.google.gson.annotations.SerializedName;

public enum OutgoingSdsType {
    @SerializedName("group")
    GROUP,

    @SerializedName("ack")
    INDIVIDUAL_ACK,

    @SerializedName("no-ack")
    INDIVIDUAL_NO_ACK
}
