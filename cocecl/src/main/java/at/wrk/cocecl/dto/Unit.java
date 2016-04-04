package at.wrk.cocecl.dto;

import java.io.Serializable;

public class Unit implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String call;
    private UnitState state;
}
