package me.vicasong.autoconfig.aot;

import me.vicasong.CliApp;
import me.vicasong.cmd.base.AbstractCommands;
import me.vicasong.valid.PathFormatValidator;
import com.sun.jna.Pointer;

import org.springframework.context.annotation.Configuration;
import org.springframework.nativex.hint.FieldHint;
import org.springframework.nativex.hint.JdkProxyHint;
import org.springframework.nativex.hint.MethodHint;
import org.springframework.nativex.hint.NativeHint;
import org.springframework.nativex.hint.ResourceHint;
import org.springframework.nativex.hint.TypeAccess;
import org.springframework.nativex.hint.TypeHint;

/**
 * AOT Hits
 *
 * @author vicasong
 * @since 2022-08-04 15:27
 */
@Configuration
@NativeHint(
        imports = {
                HitAnnotationConfig.AppReflect.class,
                HitAnnotationConfig.JNIAccess.class,
                HitAnnotationConfig.JlineAccess.class,
        }
        ,
        options = {
                "-H:+ReportExceptionStackTraces"
        }
)
public class HitAnnotationConfig {

    @TypeHint(
            // picocli help mix
            typeNames = "picocli.CommandLine$AutoHelpMixin",
            access = {
                    TypeAccess.DECLARED_FIELDS,
            })
    @TypeHint(
            // Commands spec field
            types = {AbstractCommands.class},
            fields = @FieldHint(name = "spec", allowWrite = true)
    )
    @TypeHint(
            types = {
                    // Validators
                    PathFormatValidator.class,
                    // Logback extends
                    CliApp.ProcessIdConverter.class
            },
            access = {
                    TypeAccess.PUBLIC_CONSTRUCTORS,
                    TypeAccess.PUBLIC_METHODS
            }
    )
    static class AppReflect {

    }


    // JNI Support
    @TypeHint(
            typeNames = {
                    "com.sun.jna.Native",
                    "java.lang.Class",
                    "java.lang.reflect.Method",
                    "java.lang.String",
                    "java.nio.Buffer",
                    "java.nio.ByteBuffer",
                    "java.nio.CharBuffer",
                    "java.nio.ShortBuffer",
                    "java.nio.IntBuffer",
                    "java.nio.LongBuffer",
                    "java.nio.FloatBuffer",
                    "java.nio.DoubleBuffer",
                    "com.sun.jna.Structure$ByValue",
                    "com.sun.jna.WString",
                    "com.sun.jna.NativeMapped",
                    "com.sun.jna.NativeMapped",
                    "com.sun.jna.IntegerType",
                    "com.sun.jna.PointerType",
                    "com.sun.jna.JNIEnv",
                    "com.sun.jna.Native$ffi_callback",
                    "com.sun.jna.FromNativeConverter",
                    "com.sun.jna.Callback",
                    "com.sun.jna.CallbackReference$AttachOptions",
                    "com.sun.jna.CallbackReference",
                    "com.sun.jna.Structure$FFIType",
                    "com.sun.jna.NativeLong",
                    "com.sun.jna.ptr.PointerByReference",
            },
            access = {
                    TypeAccess.JNI,
                    TypeAccess.DECLARED_CONSTRUCTORS,
                    TypeAccess.DECLARED_METHODS
            }
    )
    @TypeHint(
            typeNames = "com.sun.jna.Structure$FFIType$FFITypes",
            access = {
                    TypeAccess.JNI,
                    TypeAccess.DECLARED_CONSTRUCTORS,
                    TypeAccess.DECLARED_METHODS
            },
            fields = {
                    @FieldHint(name = "ffi_type_void", allowWrite = true),
                    @FieldHint(name = "ffi_type_float", allowWrite = true),
                    @FieldHint(name = "ffi_type_double", allowWrite = true),
                    @FieldHint(name = "ffi_type_longdouble", allowWrite = true),
                    @FieldHint(name = "ffi_type_uint8", allowWrite = true),
                    @FieldHint(name = "ffi_type_sint8", allowWrite = true),
                    @FieldHint(name = "ffi_type_uint16", allowWrite = true),
                    @FieldHint(name = "ffi_type_sint16", allowWrite = true),
                    @FieldHint(name = "ffi_type_uint32", allowWrite = true),
                    @FieldHint(name = "ffi_type_sint32", allowWrite = true),
                    @FieldHint(name = "ffi_type_uint64", allowWrite = true),
                    @FieldHint(name = "ffi_type_sint64", allowWrite = true),
                    @FieldHint(name = "ffi_type_pointer", allowWrite = true)
            }
    )
    @TypeHint(
            typeNames = "com.sun.jna.IntegerType",
            access = {
                    TypeAccess.JNI,
                    TypeAccess.DECLARED_CONSTRUCTORS,
                    TypeAccess.DECLARED_METHODS
            },
            fields = @FieldHint(name = "value", allowWrite = true)
    )
    @TypeHint(
            typeNames = "com.sun.jna.PointerType",
            access = {
                    TypeAccess.JNI,
                    TypeAccess.DECLARED_CONSTRUCTORS,
                    TypeAccess.DECLARED_METHODS
            },
            fields = @FieldHint(name = "pointer", allowWrite = true)
    )
    @TypeHint(
            typeNames = "java.lang.Void",
            access = {
                    TypeAccess.JNI,
                    TypeAccess.DECLARED_CONSTRUCTORS,
                    TypeAccess.DECLARED_METHODS
            },
            fields = @FieldHint(name = "TYPE")
    )
    @TypeHint(
            typeNames = {
                    "java.lang.Boolean",
                    "java.lang.Byte",
                    "java.lang.Character",
                    "java.lang.Short",
                    "java.lang.Integer",
                    "java.lang.Long",
                    "java.lang.Float",
                    "java.lang.Double",
            },
            access = {
                    TypeAccess.JNI,
                    TypeAccess.DECLARED_CONSTRUCTORS,
                    TypeAccess.DECLARED_METHODS
            },
            fields = {
                    @FieldHint(name = "TYPE"),
                    @FieldHint(name = "value", allowWrite = true),
            }
    )
    @TypeHint(
            typeNames = "com.sun.jna.Pointer",
            access = {
                    TypeAccess.JNI,
                    TypeAccess.DECLARED_CONSTRUCTORS,
                    TypeAccess.DECLARED_METHODS
            },
            fields = @FieldHint(name = "peer", allowWrite = true)
    )
    @TypeHint(
            typeNames = "com.sun.jna.Structure",
            access = {
                    TypeAccess.JNI,
                    TypeAccess.DECLARED_CONSTRUCTORS,
                    TypeAccess.DECLARED_METHODS
            },
            fields = {
                    @FieldHint(name = "memory", allowWrite = true),
                    @FieldHint(name = "typeInfo", allowWrite = true)
            },
            methods = {
                    @MethodHint(name = "newInstance", parameterTypes = {Class.class, Pointer.class}),
                    @MethodHint(name = "newInstance", parameterTypes = {Class.class, long.class}),
                    @MethodHint(name = "newInstance", parameterTypes = {Class.class}),
            }
    )
    @ResourceHint(
            patterns = {
                    "com/sun/jna/.*jni.*",
                    "META-INF/services/jdk.*"
            }
    )
    @TypeHint(
            typeNames = {
                    "com.sun.jna.CallbackReference",
                    "com.sun.jna.Klass",
                    "com.sun.jna.Native",
                    "com.sun.jna.NativeLong",
                    "com.sun.jna.ptr.PointerByReference",
                    "com.sun.jna.ptr.IntByReference",
                    "java.util.Base64$Decoder",
            },
            access = {
                    TypeAccess.DECLARED_CONSTRUCTORS
            }
    )
    @TypeHint(
            typeNames = "com.sun.jna.Structure",
            access = {
                    TypeAccess.DECLARED_CONSTRUCTORS
            },
            fields = {
                    @FieldHint(name = "memory", allowWrite = true),
                    @FieldHint(name = "typeInfo")
            },
            methods = {
                    @MethodHint(name = "newInstance", parameterTypes = {Class.class, Pointer.class}),
                    @MethodHint(name = "newInstance", parameterTypes = {Class.class, long.class}),
                    @MethodHint(name = "newInstance", parameterTypes = {Class.class}),
            }
    )
    @JdkProxyHint(types = {
            com.sun.jna.Library.class,
            com.sun.jna.Callback.class
    })
    static class JNIAccess {
    }


    // JLine Support
    @TypeHint(
            typeNames = {
                    "org.jline.terminal.impl.jna.win.Kernel32",
                    "org.jline.terminal.impl.jna.win.JnaWinSysTerminal",
                    "org.jline.terminal.impl.jna.linux.LinuxNativePty",
                    "org.jline.terminal.impl.jna.osx.OsxNativePty",
            },
            access = {
                    TypeAccess.JNI,
                    TypeAccess.DECLARED_CONSTRUCTORS,
                    TypeAccess.PUBLIC_CONSTRUCTORS,
                    TypeAccess.PUBLIC_METHODS,
                    TypeAccess.DECLARED_METHODS
            }
    )
    @TypeHint(
            typeNames =  {
                    "org.jline.terminal.impl.jna.win.Kernel32$INPUT_RECORD",
                    "org.jline.terminal.impl.jna.win.Kernel32$CHAR_INFO",
                    "org.jline.terminal.impl.jna.win.Kernel32$CONSOLE_CURSOR_INFO",
                    "org.jline.terminal.impl.jna.win.Kernel32$CONSOLE_SCREEN_BUFFER_INFO",
                    "org.jline.terminal.impl.jna.win.Kernel32$COORD",
                    "org.jline.terminal.impl.jna.win.Kernel32$INPUT_RECORD",
                    "org.jline.terminal.impl.jna.win.Kernel32$INPUT_RECORD$EventUnion",
                    "org.jline.terminal.impl.jna.win.Kernel32$KEY_EVENT_RECORD",
                    "org.jline.terminal.impl.jna.win.Kernel32$MOUSE_EVENT_RECORD",
                    "org.jline.terminal.impl.jna.win.Kernel32$WINDOW_BUFFER_SIZE_RECORD",
                    "org.jline.terminal.impl.jna.win.Kernel32$MENU_EVENT_RECORD",
                    "org.jline.terminal.impl.jna.win.Kernel32$FOCUS_EVENT_RECORD",
                    "org.jline.terminal.impl.jna.win.Kernel32$SMALL_RECT",
                    "org.jline.terminal.impl.jna.win.Kernel32$UnionChar",
            },
            access = {
                    TypeAccess.PUBLIC_CONSTRUCTORS,
                    TypeAccess.DECLARED_CLASSES,
                    TypeAccess.DECLARED_CONSTRUCTORS,
                    TypeAccess.DECLARED_FIELDS,
                    TypeAccess.DECLARED_METHODS
            }
    )
    @JdkProxyHint(
            typeNames = {
                    "org.jline.terminal.impl.jna.win.Kernel32",
                    "org.jline.terminal.impl.jna.linux.CLibrary",
                    "org.jline.terminal.impl.jna.osx.CLibrary",
            }
    )
    @ResourceHint(patterns = {
            "org/jline/utils/.*",
            "META-INF/services/.*"
    })
    static class JlineAccess {

    }
    
}
