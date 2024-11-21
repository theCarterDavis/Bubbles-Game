public enum TileType {
    VELMOD {

        @Override
        public TileComponent decorate(TileComponent tile) {
            return new VelmodDecorator(tile,1,2,3); // Default value
        }

        @Override
        public TileComponent decorate(TileComponent tile, Object... params) {
            if (params.length > 0 && params[0] instanceof Integer) {
                return new VelmodDecorator(tile, (int)params[0],(double)params[1],(double)params[2]);
            }
            return decorate(tile);
        }
    },
    START {
        @Override
        public TileComponent decorate(TileComponent tile) {
            return new StartDecorator(tile);
        }

        @Override
        public TileComponent decorate(TileComponent tile, Object... params) {
            return decorate(tile); // START doesn't use parameters, so just call the regular decorate
        }
    },
    DEATH {
        @Override
        public TileComponent decorate(TileComponent tile) {
            return new KillDecorator(tile);
        }
        @Override
        public TileComponent decorate(TileComponent tile, Object... params) {
            return decorate(tile); // START doesn't use parameters, so just call the regular decorate
        }
    },

    GOAL {
        @Override
        public TileComponent decorate(TileComponent tile) {
            return new GoalDecorator(tile);
        }
        @Override
        public TileComponent decorate(TileComponent tile, Object... params) {
            return decorate(tile); // START doesn't use parameters, so just call the regular decorate
        }
    },
    PUSH {
        @Override
        public TileComponent decorate(TileComponent tile) {
            return new PushDecorator(tile,1,2,false); // Default value
        }
        @Override
        public TileComponent decorate(TileComponent tile, Object... params) {
            if (params.length > 0 && params[0] instanceof Integer) {
                return new PushDecorator(tile, (Integer)params[0],(Double)params[1],(Boolean)params[2]);
            }
            return decorate(tile);
        }

    },
    PULL {
        @Override
        public TileComponent decorate(TileComponent tile) {
            return new PullDecorator(tile,1,2,false); // Default value
        }
        @Override
        public TileComponent decorate(TileComponent tile, Object... params) {
            if (params.length > 0 && params[0] instanceof Integer) {
                return new PullDecorator(tile, (Integer)params[0],(Double)params[1],(Boolean)params[2]);
            }
            return decorate(tile);
        }

    },
    ACTIVATE {
        @Override
        public TileComponent decorate(TileComponent tile) {
            return new ActivateDecorator(tile, "default"); // Default target name
        }

        @Override
        public TileComponent decorate(TileComponent tile, Object... params) {
            if (params.length > 0 && params[0] instanceof String) {
                return new ActivateDecorator(tile, (String)params[0]);
            }
            return decorate(tile);
        }
    },
    BREAK {
        @Override
        public TileComponent decorate(TileComponent tile) {
            return new BreaksDecorator(tile, 0.0); // Default target name
        }

        @Override
        public TileComponent decorate(TileComponent tile, Object... params) {
            if (params.length > 0 && params[0] instanceof Double) {
                return new BreaksDecorator(tile, (Double)params[0]);
            }
            return decorate(tile);
        }
    },
    ERASE {
        @Override
        public TileComponent decorate(TileComponent tile) {
            return new EraseDecorator(tile); // Default target name
        }

        @Override
        public TileComponent decorate(TileComponent tile, Object... params) {
            if (params.length > 0 && params[0] instanceof Double) {
                return new EraseDecorator(tile);
            }
            return decorate(tile);
        }
    },
    TIMER {
        @Override
        public TileComponent decorate(TileComponent tile) {
            return new TimerDecorator(tile, 1.0); // Default target name
        }

        @Override
        public TileComponent decorate(TileComponent tile, Object... params) {
            if (params.length > 0 && params[0] instanceof Double) {
                return new TimerDecorator(tile, (Double)params[0]);
            }
            return decorate(tile);
        }
    }
;

    // Abstract method declaration should be here, inside the enum body
    public abstract TileComponent decorate(TileComponent tile);
    public abstract TileComponent decorate(TileComponent tile, Object... params);


    @Override
    public String toString() {
        return name().toLowerCase();
    }

    // Helper method to get TileType from string representation
    public static TileType fromString(String str) {
        for (TileType type : TileType.values()) {
            if (type.toString().equalsIgnoreCase(str)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid tile type: " + str);
    }
}