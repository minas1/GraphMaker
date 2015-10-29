package model;

/**
 * Created by minas on 12/10/2015.
 */
public enum SymbolType {

    NO_SYMBOL {
        @Override
        public String toString() { return "No symbol"; }
    },

    FILLED_CIRCLE {
        @Override
        public String toString() { return "Filled circle"; }
    },

    EMPTY_CIRCLE {
        @Override
        public String toString() { return "Empty circle"; }
    }
}
