package org.dyndns.fzoli.rccar.model.bridge;

/**
 * A hoszt és vezérlő tárolójában az a közös,
 * hogy mindkettőnek van név paramétere.
 * A név alapján lehet a tároló listában keresni.
 * @author zoli
 */
interface Storage {
    
    /**
     * Megadja a kliens nevét.
     */
    public String getName();
    
}
