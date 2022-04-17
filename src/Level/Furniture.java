package Level;

public class Furniture {
    // each item of furniture is defined by 2D array containing TileTypes

    TileType[][] tileArray;

    public Furniture(TileType[][] tileArray) {
        this.tileArray = tileArray;
    }

    public static Furniture bed() {
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

        return new Furniture(obj);
    }

    public static Furniture cupboard() {
        TileType[][] obj = {
                { TileType.CABINET, TileType.CABINET, TileType.CABINET, TileType.CABINET, TileType.CABINET },
                { TileType.CABINET, TileType.CABINET, TileType.CABINET, TileType.CABINET, TileType.CABINET },
                { TileType.CABINET, TileType.CABINET, TileType.CABINET, TileType.CABINET, TileType.CABINET },
        };

        return new Furniture(obj);
    }
}
