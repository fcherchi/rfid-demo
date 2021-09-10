package com.fcherchi.demo.events.impl;

import org.apache.commons.lang3.StringUtils;

import java.util.BitSet;

public class Sgtin96 {

    private SgtinFilterValue filter;
    private SgtinPartitionValue partition;
    private long companyPrefix;
    private long itemReference;
    private long serial;

    private BitSet code;

    public Sgtin96(SgtinFilterValue filter, SgtinPartitionValue partition, long companyPrefix, long itemReference, long serial) {
        super();

        code = new BitSet(96);

        setFilter(filter);
        setPartition(partition);
        setCompanyPrefix(companyPrefix);
        setItemReference(itemReference);
        setSerial(serial);
    }

    public Sgtin96(String hexNumber) {
        if ((hexNumber == null) || (hexNumber.length() != 24)) {
            throw new EPCException("the hexNumber: " + hexNumber + ", is not valid as a valid SGTIN_96 code");
        }

        String binValue = "";
        for (int i = 0; i < 24; i++) {
            Integer digitValue = Integer.decode("#" + hexNumber.charAt(i));

            String digitBinValue = StringUtils.leftPad(Integer.toBinaryString(digitValue), 4, "0");

            binValue = binValue + digitBinValue;
        }

        //set the EPC Header
        String epcHeader = binValue.substring(0, 8);
        if (Integer.parseInt(epcHeader, 2) != 48) {
            throw new EPCException("the epcHeader: " + epcHeader + ", is not valid as a valid SGTIN_96 Header");
        }
        //set the EPC Filter
        String filter = binValue.substring(8, 11);
        SgtinFilterValue filterValue = SgtinFilterValue.getSGTINFilterValue(Integer.parseInt(filter, 2));
        if (filterValue == null) {
            throw new EPCException("the filterValue: " + filter + ", is not valid as a valid SGTIN_96 Filter");
        }
        setFilter(filterValue);
        //set the EPC Partition
        String partition = binValue.substring(11, 14);
        SgtinPartitionValue partitionValue = SgtinPartitionValue.getSGTINPartitionValue(Integer.parseInt(partition, 2));
        if (partitionValue == null) {
            throw new EPCException("the partitionValue: " + partition + ", is not valid as a valid SGTIN_96 Partition");
        }
        setPartition(partitionValue);
        //set the EPC Company Prefix
        String companyPrefix = binValue.substring(14, 14 + getPartition().getCompanyPrefixBits());
        setCompanyPrefix(Integer.parseInt(companyPrefix, 2));
        //set the EPC Item Reference
        String itemReference = binValue.substring(14 + getPartition().getCompanyPrefixBits(), 14 + getPartition().getCompanyPrefixBits() + getPartition().getItemReferenceBits());
        setItemReference(Integer.parseInt(itemReference, 2));
        //set the EPC Serial
        String serial = binValue.substring(14 + getPartition().getCompanyPrefixBits() + getPartition().getItemReferenceBits());
        setSerial(Integer.parseInt(serial, 2));
    }

    public int getEpcHeader() {
        return 48;
    }

    public SgtinFilterValue getFilter() {
        return filter;
    }

    private void setFilter(SgtinFilterValue filter) {
        this.filter = filter;
    }

    public SgtinPartitionValue getPartition() {
        return partition;
    }

    private void setPartition(SgtinPartitionValue partition) {
        this.partition = partition;
    }

    public long getCompanyPrefix() {
        return companyPrefix;
    }

    private void setCompanyPrefix(long companyPrefix) {
        long minCompanyPrefixValue = 0;
        long maxCompanyPrefixValue = (long) Math.pow(2, getPartition().getCompanyPrefixBits());

        if ((companyPrefix < minCompanyPrefixValue) || (companyPrefix > maxCompanyPrefixValue)) {
            throw new EPCException("the companyPrefix: " + companyPrefix + ", is not valid for the partition value: " + getPartition());
        }

        this.companyPrefix = companyPrefix;
    }

    public long getItemReference() {
        return itemReference;
    }

    private void setItemReference(long itemReference) {
        long minItemReferenceValue = 0;
        long maxItemReferenceValue = (long) Math.pow(2, getPartition().getItemReferenceBits());

        if ((itemReference < minItemReferenceValue) || (itemReference > maxItemReferenceValue)) {
            throw new EPCException("the itemReference: " + itemReference + ", is not valid for the partition value: " + getPartition());
        }

        this.itemReference = itemReference;
    }

    public long getSerial() {
        return serial;
    }

    private void setSerial(long serial) {
        int minSerial = 0;
        long maxSerial = 274877906943l;

        if ((serial < minSerial) || (serial > maxSerial)) {
            throw new EPCException("the serial: " + serial + ", is not valid ");
        }

        this.serial = serial;
    }

    public BitSet getCode() {
        String strCode = toString();

        for (int i = 0; i < strCode.length(); i++) {
            code.set(i, strCode.charAt(i) == '1');
        }

        return code;
    }

    public String getBinary() {
        String epcHeader = StringUtils.leftPad(Integer.toBinaryString(getEpcHeader()), 8, "0");
        String filter = StringUtils.leftPad(Integer.toBinaryString(getFilter().getFilterValue()), 3, "0");
        String partition = StringUtils.leftPad(Integer.toBinaryString(getPartition().getPartitionValue()), 3, "0");
        String companyPrefix = StringUtils.leftPad(Long.toBinaryString(getCompanyPrefix()), getPartition().getCompanyPrefixBits(), "0");
        String itemReference = StringUtils.leftPad(Long.toBinaryString(getItemReference()), getPartition().getItemReferenceBits(), "0");
        String serial = StringUtils.leftPad(Long.toBinaryString(getSerial()), 38, "0");

        String binary = epcHeader + filter + partition + companyPrefix + itemReference + serial;

        return binary;
    }

    public String toString() {
        String hexNumber = "";
        String binary = getBinary();
        int beginIndex = 0;
        while (beginIndex < 96) {
            String digit = binary.substring(beginIndex, (beginIndex + 4));

            String hexString = Integer.toHexString(Integer.parseInt(digit, 2));

            hexNumber = hexNumber + hexString.toUpperCase();

            beginIndex = beginIndex + 4;
        }

        return hexNumber;
    }
}

