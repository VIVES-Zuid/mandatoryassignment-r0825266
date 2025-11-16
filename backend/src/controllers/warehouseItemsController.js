const WarehouseItem = require("../models/warehouseItem");

async function getWarehouseItems(req, res) {
    try {
        const items = await WarehouseItem.find();
        res.json(items);
    } catch (err) {
        res.status(500).send("Server error");
    }
}

module.exports = {
    getWarehouseItems
};
