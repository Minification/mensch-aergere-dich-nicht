package de.mariushubatschek.is;

import java.util.Objects;

public class Figurine {

    private final Player player;

    private boolean active;

    private final int index;

    public Figurine(final Player player, final int index) {
        this.player = player;
        this.index = index;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public void beat() {
        active = false;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Figurine figurine = (Figurine) o;
        return active == figurine.active &&
                index == figurine.index &&
                player.equals(figurine.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, index, active);
    }

    @Override
    public String toString() {
        return "Figurine{" +
                "player=" + player +
                ", active=" + active +
                ", index=" + index +
                '}';
    }
}
