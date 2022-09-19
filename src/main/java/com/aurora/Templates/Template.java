package com.aurora.Templates;

public interface Template {
    void generate();
    byte[] getBytes();
    void cache();
    String getClassName();
}
