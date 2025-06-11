package com.memory.domain.file;

import lombok.Getter;

@Getter
public enum FileType {
    MEMBER("MEMBER"),
    MEMORY("MEMORY"),
    ;

    private final String directory;

    FileType(String directory) {
        this.directory = directory;
    }
}