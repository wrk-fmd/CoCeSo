package at.wrk.cocecl.dto.login;

import at.wrk.cocecl.dto.Answer;

import java.io.Serializable;

public class LoginAnswer extends Answer implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer session;

    public Integer getSession() {
        return session;
    }

    public void setSession(Integer session) {
        this.session = session;
    }
}
