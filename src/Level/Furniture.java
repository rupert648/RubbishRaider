package Level;

public abstract class Furniture {
    // each item of furniture is defined by 2D array containing TileTypes

    public static TileType[][] bed() {
        // TODO: store as CSVs and load in on program start

        TileType[][] obj = {
                {TileType.BED_DUVET, TileType.BED_PILLOW, TileType.BED_PILLOW, TileType.BED_DUVET, TileType.BED_PILLOW, TileType.BED_PILLOW, TileType.BED_DUVET },
                {TileType.BED_DUVET, TileType.BED_PILLOW, TileType.BED_PILLOW, TileType.BED_DUVET, TileType.BED_PILLOW, TileType.BED_PILLOW, TileType.BED_DUVET },
                {TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET },
                {TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET },
                {TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET },
                {TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET },
                {TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET },
                {TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET },
                {TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET, TileType.BED_DUVET },
        };

        return obj;
    }
}
