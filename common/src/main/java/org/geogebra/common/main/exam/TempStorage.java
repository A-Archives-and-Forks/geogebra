package org.geogebra.common.main.exam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.geogebra.common.move.ggtapi.models.Material;

/**
 * Manages the ExamEnvironment's temporary materials.
 */
public class TempStorage {

    private int tempMaterialId;
    private Map<Integer, Material> tempMaterials;

    TempStorage() {
        tempMaterials = new LinkedHashMap<>();
    }

    /**
     * @return a new Material with a unique id
     */
    public Material newMaterial() {
        Material material = new Material(Material.MaterialType.ggb);
        material.setLocalID(nextTempMaterialId());
        return material;
    }

    private int nextTempMaterialId() {
        return tempMaterialId++;
    }

    /**
     * Saves a copy of the material into the tempMaterials with the correct id.
     */
    public void saveTempMaterial(Material material) {
        Material savedMaterial = tempMaterials.get(material.getLocalID());
        if (savedMaterial != null && !savedMaterial.getTitle().equals(material.getTitle())) {
            material.setLocalID(nextTempMaterialId());
        }
        tempMaterials.put(material.getLocalID(), new Material(material));
    }

    /**
     * @return A copy of the tempMaterials.
     */
    public Collection<Material> collectTempMaterials() {
        List<Material> materials = new ArrayList<>();
        for (Material mat: tempMaterials.values()) {
            materials.add(new Material(mat));
        }
        return materials;
    }

    /**
     * Reinitializes the tempMaterials and the next id is set back to 0
     */
    public void clearTempMaterials() {
        tempMaterialId = 0;
        tempMaterials = new LinkedHashMap<>();
    }

    /**
     * THIS IS A COPY OF A CHANGE IN APPS-5280. I MERGED THE iOS BRANCH
     * BEFORE THE COMMON/WEB BRANCH WAS MERGED, AND WE NEED THIS, SO
     * THIS IS WHY THIS IS HERE.
     */
    public void deleteTempMaterial(Material material) {
        tempMaterials.remove(material.getLocalID());
    }

    public boolean isEmpty() {
        return tempMaterials.isEmpty();
    }
}
