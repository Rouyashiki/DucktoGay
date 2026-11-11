package org.rouyashiki.ducktogay;

import android.util.Log;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.libxposed.api.XposedModule;

public final class DuckToGayModule extends XposedModule {
    static final String TARGET_PACKAGE = "com.eltavine.duckdetector";
    private static final String HOOK_ID = "ducktogay.text";
    private static final String TAG = "DuckToGay";

    private boolean targetProcess;
    private boolean targetPackageLoaded;
    private boolean installed;

    static boolean isTargetProcess(String name) {
        return name != null && (name.equals(TARGET_PACKAGE) || name.startsWith(TARGET_PACKAGE + ":"));
    }

    @Override
    public synchronized void onModuleLoaded(ModuleLoadedParam param) {
        targetProcess = !param.isSystemServer() && isTargetProcess(param.getProcessName());
        if (!targetProcess) detach();
    }

    @Override
    public synchronized void onPackageLoaded(PackageLoadedParam param) {
        if (!targetProcess || !TARGET_PACKAGE.equals(param.getPackageName()) || installed) return;
        targetPackageLoaded = true;
        installHooks(List.of());
    }

    @Override
    public synchronized boolean onHotReloading(HotReloadingParam param) {
        // Boolean belongs to the boot classloader. Only framework-owned Java
        // hooks hold this generation, and their retirement is handled below.
        param.setSavedInstanceState(Boolean.valueOf(targetPackageLoaded));
        return true;
    }

    @Override
    public synchronized void onHotReloaded(HotReloadedParam param) {
        targetProcess = !param.isSystemServer() && isTargetProcess(param.getProcessName());
        targetPackageLoaded = Boolean.TRUE.equals(param.getSavedInstanceState());
        if (!targetProcess || !targetPackageLoaded) {
            for (HookHandle handle : param.getOldHookHandles()) handle.unhook();
            if (!targetProcess) detach();
            return;
        }
        installHooks(param.getOldHookHandles());
        log(Log.INFO, TAG, "Hot reload completed");
    }

    private void installHooks(List<HookHandle> oldHooks) {
        List<HookHandle> stale = new ArrayList<>(oldHooks);
        int count = 0;
        try {
            Map<Executable, Hooker> hooks = TextHooks.create(
                    name -> log(Log.DEBUG, TAG, "Text interface unavailable on this Android version " + name));
            for (Map.Entry<Executable, Hooker> entry : hooks.entrySet()) {
                HookHandle previous = findOldHook(stale, entry.getKey());
                if (previous != null) {
                    previous.replaceHook(entry.getValue());
                    stale.remove(previous);
                } else {
                    hook(entry.getKey()).setId(HOOK_ID).intercept(entry.getValue());
                }
                count++;
            }
            installed = true;
            log(Log.INFO, TAG, "Installed " + count + " text hooks for " + TARGET_PACKAGE);
        } finally {
            // Retire removed hooks and any old callbacks left after a failed
            // replacement. In-flight calls keep the framework's old snapshot.
            for (HookHandle handle : stale) handle.unhook();
        }
    }

    private static HookHandle findOldHook(List<HookHandle> handles, Executable executable) {
        for (HookHandle handle : handles) {
            if (HOOK_ID.equals(handle.getId()) && executable.equals(handle.getExecutable())) return handle;
        }
        return null;
    }
}
