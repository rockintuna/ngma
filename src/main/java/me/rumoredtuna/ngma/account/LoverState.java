package me.rumoredtuna.ngma.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

//@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonSerialize(using = LoverStateSerializer.class)
public enum LoverState {
    NOTHING, WAITING, COUPLED;

    public boolean hasWaiters;

    public boolean isHasWaiters() {
        return hasWaiters;
    }

    public boolean getHasWaiters() {
        return this.hasWaiters;
    }

    public void setHasWaiters(boolean hasWaiters) {
        this.hasWaiters = hasWaiters;
    }

}
