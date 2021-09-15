package com.example.hw11;

import lombok.Data;

@Data
public class PutJob {
    int priority;
    int delay;
    int ttr;
    Object data;
}
