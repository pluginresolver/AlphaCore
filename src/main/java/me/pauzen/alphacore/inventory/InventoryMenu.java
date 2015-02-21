/*
 *  Created by Filip P. on 2/20/15 7:01 PM.
 */

package me.pauzen.alphacore.inventory;

import me.pauzen.alphacore.inventory.misc.ClickType;
import me.pauzen.alphacore.inventory.elements.Element;
import me.pauzen.alphacore.inventory.elements.InteractableElement;
import me.pauzen.alphacore.inventory.misc.Coordinate;
import me.pauzen.alphacore.players.CorePlayer;
import me.pauzen.alphacore.utils.InvisibleID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public abstract class InventoryMenu {

    private Map<Coordinate, Element> elementMap = new HashMap<>();

    private Map<Coordinate, Predicate<InventoryClickEvent>> allowedClicking = new HashMap<>();

    private Inventory   inventory;
    private InvisibleID inventoryID;

    /**
     * Creates new Inventory with null holder, size and name as specified with the name getting an invisible unique ID appended to the end of it.
     * @param name The desired name of the inventory.
     * @param size The desired size of the inventory, must be a multiple of 9. (size % 9 == 0) condition must be met.
     */
    public InventoryMenu(String name, int size) {
        registerElements();
        fillRemaining();
        this.inventory = Bukkit.createInventory(null, size, name + (inventoryID = InvisibleID.generate()).getId());
        InventoryManager.getManager().registerMenu(this);
    }

    /**
     * Processes InventoryClickEvent and looks for any interactable elements that the player clicked.
     * @param event InventoryClickEvent to process.
     */
    public final void process(InventoryClickEvent event) {
        
        Coordinate clicked = toCoordinate(event.getRawSlot());

        Element clickedElement = elementMap.get(clicked);

        if (clickedElement instanceof InteractableElement) {
            InteractableElement interactableElement = (InteractableElement) clickedElement;

            interactableElement.onClick((Player) event.getWhoClicked(), event.getAction() == InventoryAction.PICKUP_ALL ? ClickType.INVENTORY_LEFT :
                    event.getAction() == InventoryAction.PICKUP_HALF ? ClickType.INVENTORY_RIGHT : ClickType.OTHER);
        } else {
            if (!shouldAllowClick(event)) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
            }
        }
    }

    /**
     * Returns whether the InventoryClickEvent should be cancelled or not.
     * @param e The InventoryClickEvent to get the outcome of.
     * @return Whether or not to cancel the event.
     */
    public boolean shouldAllowClick(InventoryClickEvent e) {

        if (e.getRawSlot() >= inventory.getSize()) {
            return true;
        }

        Coordinate inventoryCoordinate = toCoordinate(e.getRawSlot());

        return allowedClicking.get(inventoryCoordinate).test(e);
    }

    /**
     * Where to register elements and such.
     */
    public abstract void registerElements();

    /**
     * Fills unregistered element slots with blank elements.
     */
    public void fillRemaining() {
        for (int i = 0; i < inventory.getContents().length; i++) {
            
            Coordinate coordinate = toCoordinate(i);
            
            elementMap.putIfAbsent(coordinate, Element.BLANK_ELEMENT);
        }
    }

    /**
     * Shows the inventory to a player.
     * @param player Player to show the inventory to.
     */
    public void show(Player player) {
        player.openInventory(inventory);
    }

    /**
     * Shows the inventory to a player.
     * @param corePlayer Player to show the inventory to.
     */
    public void show(CorePlayer corePlayer) {
        show(corePlayer.getPlayer());
    }

    /**
     * Updates an element in an already open inventory.
     * @param coordinate Where to execute the update.
     * @param item What to update the item to.
     */
    public void updateElement(Coordinate coordinate, ItemStack item) {
        inventory.setItem(coordinate.toSlot(), item);
    }

    /**
     * Updates an element in an already open inventory.
     * @param coordinate Where to execute the update.
     */
    public void updateElement(Coordinate coordinate) {
        updateElement(coordinate, elementMap.get(coordinate).getRepresentation());
    }

    /**
     * Creates a condition that must be met if shouldAllowClick were to return true.
     * @param inventoryCoordinate The coordinates where the allowance condition should be checking.
     * @param predicate The condition that must be met.
     */
    public void createAllowanceConditionForClickingAt(Coordinate inventoryCoordinate, Predicate<InventoryClickEvent> predicate) {
        allowedClicking.put(inventoryCoordinate, predicate);
    }

    /**
     * Creates a condition that must be met if shouldAllowClick were to return true.
     * @param predicate The condition that must be met.
     */
    public void createAllowanceConditionForClickingAt(int x, int y, Predicate<InventoryClickEvent> predicate) {
        createAllowanceConditionForClickingAt(new Coordinate(x, y), predicate);
    }

    private static Coordinate toCoordinate(int x, int y) {
        return Coordinate.coordinate(x, y);
    }
    
    private static Coordinate toCoordinate(int inventorySlot) {
        return Coordinate.fromSlot(inventorySlot);
    }

    public String getName() {
        return inventory.getTitle();
    }

    public String getID() {
        return inventoryID.getId();
    }

    public Element getElementAt(Coordinate coordinate) {
        return elementMap.get(coordinate);
    }
    
    public Element getElementAt(int x, int y) {
        return elementMap.get(new Coordinate(x, y));
    }
}
