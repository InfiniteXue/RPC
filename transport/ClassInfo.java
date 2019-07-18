package rpc.transport;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Setter
@Getter
@ToString
public class ClassInfo implements Serializable {

    // 使用ObjectEncoder和ObjectDecoder编解码时需要实现Serializable接口
    private static final long serialVersionUID = -7770555752417527829L;

    private String className;
    private String methodName;
    private Class<?>[] argTypes;
    private Object[] args;

}
