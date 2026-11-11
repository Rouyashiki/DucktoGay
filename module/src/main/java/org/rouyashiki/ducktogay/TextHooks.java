package org.rouyashiki.ducktogay;

import android.app.Notification;
import android.app.NotificationChannel;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import io.github.libxposed.api.XposedInterface;

final class TextHooks {
    private TextHooks() {}

    static Map<Executable, XposedInterface.Hooker> create(Consumer<String> missingClass) {
        Map<Executable, XposedInterface.Hooker> hooks = new LinkedHashMap<>();
        List<String> resourceMethods = Arrays.asList("getText", "getString", "getQuantityText",
                "getQuantityString", "getStringArray", "getTextArray");
        for (Method method : Resources.class.getDeclaredMethods()) {
            if (resourceMethods.contains(method.getName())) {
                hooks.put(method, chain -> TextRewriter.replaceResource(chain.proceed()));
            }
        }

        addRendering(hooks, missingClass, "android.graphics.BaseCanvas",
                "nDrawText", "nDrawTextRun", "nDrawTextOnPath");
        addRendering(hooks, missingClass, "android.graphics.BaseRecordingCanvas",
                "nDrawText", "nDrawTextRun", "nDrawTextOnPath");
        addRendering(hooks, missingClass, "android.graphics.Paint",
                "nGetTextAdvances", "nBreakText", "nGetStringBounds", "nGetCharArrayBounds",
                "nGetRunAdvance", "nGetRunCharacterAdvance", "nGetOffsetForAdvance",
                "nGetTextRunCursor", "nGetTextPath", "nGetFontMetricsIntForText");
        addRendering(hooks, missingClass, "android.graphics.text.MeasuredText$Builder", "nBuildMeasuredText");
        addRendering(hooks, missingClass, "android.graphics.text.MeasuredText", "nGetBounds", "nGetExtent");
        addRendering(hooks, missingClass, "android.graphics.text.TextRunShaper", "nativeShapeTextRun");

        addDisplay(hooks, Notification.Builder.class, 0, "setContentTitle", "setContentText",
                "setSubText", "setContentInfo", "setTicker", "setSettingsText", "setShortCriticalText");
        addDisplay(hooks, NotificationChannel.class, 0, "setName", "setDescription");
        for (Constructor<?> constructor : NotificationChannel.class.getDeclaredConstructors()) {
            Class<?>[] types = constructor.getParameterTypes();
            if (types.length == 3 && types[0] == String.class && types[1] == CharSequence.class
                    && types[2] == int.class) hooks.put(constructor, displayArgument(1));
        }
        addDisplay(hooks, Intent.class, 1, "createChooser");
        addDisplay(hooks, Toast.class, 1, "makeText");
        addDisplay(hooks, Toast.class, 0, "setText");
        addDisplay(hooks, RemoteViews.class, 1, "setTextViewText");
        return hooks;
    }

    private static void addRendering(Map<Executable, XposedInterface.Hooker> hooks,
            Consumer<String> missingClass, String className, String... names) {
        Class<?> owner;
        try {
            owner = Class.forName(className, false, Resources.class.getClassLoader());
        } catch (ClassNotFoundException exception) {
            missingClass.accept(className);
            return;
        }
        List<String> selected = Arrays.asList(names);
        for (Method method : owner.getDeclaredMethods()) {
            if (!selected.contains(method.getName())) continue;
            Class<?>[] types = method.getParameterTypes();
            for (int index = 0; index < types.length; index++) {
                if (types[index] == String.class || types[index] == char[].class) {
                    hooks.put(method, renderingArgument(index));
                    break;
                }
            }
        }
    }

    private static void addDisplay(Map<Executable, XposedInterface.Hooker> hooks,
            Class<?> owner, int index, String... names) {
        List<String> selected = Arrays.asList(names);
        for (Method method : owner.getDeclaredMethods()) {
            Class<?>[] types = method.getParameterTypes();
            if (selected.contains(method.getName()) && types.length > index
                    && CharSequence.class.isAssignableFrom(types[index])) {
                hooks.put(method, displayArgument(index));
            }
        }
    }

    static XposedInterface.Hooker displayArgument(int index) {
        return chain -> {
            CharSequence original = (CharSequence) chain.getArg(index);
            CharSequence replacement = TextRewriter.replace(original);
            if (replacement == original) return chain.proceed();
            Object[] args = chain.getArgs().toArray();
            args[index] = replacement;
            return chain.proceed(args);
        };
    }

    static XposedInterface.Hooker renderingArgument(int index) {
        return chain -> {
            Object original = chain.getArg(index);
            Object replacement = TextRewriter.replaceIndexed(original);
            if (replacement == original) return chain.proceed();
            Object[] args = chain.getArgs().toArray();
            args[index] = replacement;
            return chain.proceed(args);
        };
    }
}
