package com.fcherchi.demo.events.impl;


public enum SgtinFilterValue {
    OTHERS(0),
    ITEM(1),
    CASE(2),
    RESERVED1(3),
    PALLET(4),
    RESERVED2(5),
    UNITLOAD(6),
    SUBITEM(7);

    private int filterValue;

    SgtinFilterValue(int filterValue) {
        this.filterValue = filterValue;
    }

    public int getFilterValue() {
        return filterValue;
    }

    public static SgtinFilterValue getSGTINFilterValue(int filterValue) {
        SgtinFilterValue lookingFor = null;

        switch (filterValue) {
            case 0:
                lookingFor = OTHERS;
                break;
            case 1:
                lookingFor = ITEM;
                break;
            case 2:
                lookingFor = CASE;
                break;
            case 3:
                lookingFor = RESERVED1;
                break;
            case 4:
                lookingFor = PALLET;
                break;
            case 5:
                lookingFor = RESERVED2;
                break;
            case 6:
                lookingFor = UNITLOAD;
                break;
            case 7:
                lookingFor = SUBITEM;
                break;
        }

        return lookingFor;
    }
}

