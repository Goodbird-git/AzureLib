package mod.azure.azurelib.cache;

import mod.azure.azurelib.AzureLib;
import net.minecraft.client.resources.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.FMLFolderResourcePack;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileZipLoading {

    public static List<IResourcePack> getPacks() {
        try {
            Field field = FMLClientHandler.class.getDeclaredField("resourcePackList");
            field.setAccessible(true);

            Object value = field.get(FMLClientHandler.instance());

            if (value instanceof List<?>) {
                @SuppressWarnings("unchecked") // Safe, as we check the type
                List<IResourcePack> resourcePacks = (List<IResourcePack>) value;
                return resourcePacks;
            } else {
                AzureLib.LOGGER.error("The field 'resourcePackList' is not of type List<IResourcePack>.");
            }
        } catch (NoSuchFieldException e) {
            AzureLib.LOGGER.error("The field 'resourcePackList' does not exist in FMLClientHandler.", e);
        } catch (IllegalAccessException e) {
            AzureLib.LOGGER.error("Failed to access the field 'resourcePackList' in FMLClientHandler.", e);
        } catch (ClassCastException e) {
            AzureLib.LOGGER.error("Failed to cast the field 'resourcePackList' to List<IResourcePack>.", e);
        } catch (Exception e) {
            AzureLib.LOGGER.error("Unexpected error while accessing resource pack list.", e);
        }

        return Collections.emptyList();
    }

    public static List<ResourceLocation> getLocations(IResourcePack pack, String folder, Predicate<String> predicate) {
        if (pack instanceof LegacyV2Adapter) {
            LegacyV2Adapter adapter = (LegacyV2Adapter) pack;
            Field packField = null;

            for (Field field : adapter.getClass().getDeclaredFields()) {
                if (field.getType() == IResourcePack.class) {
                    packField = field;

                    break;
                }
            }

            if (packField != null) {
                packField.setAccessible(true);

                try {
                    return FileZipLoading.getLocations((IResourcePack) packField.get(adapter), folder, predicate);
                } catch (Exception e) {
                }
            }
        }

        List<ResourceLocation> locations = new ArrayList<>();

        if (pack instanceof FolderResourcePack) {
            FileZipLoading.handleFolderResourcePack((FolderResourcePack) pack, folder, predicate, locations);
        } else if (pack instanceof FileResourcePack) {
            FileZipLoading.handleZipResourcePack((FileResourcePack) pack, folder, predicate, locations);
        }

        return locations;
    }

    public static void handleFolderResourcePack(FolderResourcePack folderPack, String folder, Predicate<String> predicate,
                                          List<ResourceLocation> locations) {
        Field fileField = null;

        for (Field field : AbstractResourcePack.class.getDeclaredFields()) {
            if (field.getType() == File.class) {
                fileField = field;

                break;
            }
        }

        if (fileField != null) {
            fileField.setAccessible(true);

            try {
                File file = (File) fileField.get(folderPack);
                Set<String> domains = folderPack.getResourceDomains();

                if (folderPack instanceof FMLFolderResourcePack) {
                    domains.add(((FMLFolderResourcePack) folderPack).getFMLContainer().getModId());
                }

                for (String domain : domains) {
                    String prefix = "assets/" + domain + "/" + folder;
                    File pathFile = new File(file, prefix);

                    FileZipLoading.enumerateFiles(folderPack, pathFile, predicate, locations, domain, folder);
                }
            } catch (IllegalAccessException e) {
                AzureLib.LOGGER.error(e);
            }
        }
    }

    public static void enumerateFiles(FolderResourcePack folderPack, File parent, Predicate<String> predicate,
                                List<ResourceLocation> locations, String domain, String prefix) {
        File[] files = parent.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isFile() && predicate.test(file.getName())) {
                locations.add(new ResourceLocation(domain, prefix + "/" + file.getName()));
            } else if (file.isDirectory()) {
                FileZipLoading.enumerateFiles(folderPack, file, predicate, locations, domain, prefix + "/" + file.getName());
            }
        }
    }

    public static void handleZipResourcePack(FileResourcePack filePack, String folder, Predicate<String> predicate,
                                       List<ResourceLocation> locations) {
        Field zipField = null;

        for (Field field : FileResourcePack.class.getDeclaredFields()) {
            if (field.getType() == ZipFile.class) {
                zipField = field;

                break;
            }
        }

        if (zipField != null) {
            zipField.setAccessible(true);

            try {
                FileZipLoading.enumerateZipFile(filePack, folder, (ZipFile) zipField.get(filePack), predicate, locations);
            } catch (IllegalAccessException e) {
                AzureLib.LOGGER.error(e);
            }
        }
    }

    public static void enumerateZipFile(FileResourcePack filePack, String folder, ZipFile file, Predicate<String> predicate,
                                  List<ResourceLocation> locations) {
        Set<String> domains = filePack.getResourceDomains();
        Enumeration<? extends ZipEntry> it = file.entries();

        while (it.hasMoreElements()) {
            String name = it.nextElement().getName();

            for (String domain : domains) {
                String assets = "assets/" + domain + "/";
                String path = assets + folder + "/";

                if (name.startsWith(path) && predicate.test(name)) {
                    locations.add(new ResourceLocation(domain, name.substring(assets.length())));
                }
            }
        }
    }
}
