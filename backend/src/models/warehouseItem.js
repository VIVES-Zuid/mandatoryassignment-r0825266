const mongoose = require("mongoose");

const warehouseItemSchema = new mongoose.Schema({
    name: {
        type: String,
        required: true
    },
    quantity: {
        type: Number,
        default: 0
    },
    location: {
        type: String,
        required: true
    }
});

module.exports = mongoose.model("WarehouseItem", warehouseItemSchema);
