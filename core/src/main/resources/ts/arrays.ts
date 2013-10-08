module Verily {
    export module Arrays {
        export function zipWith(l1, l2, what) {
            return l1.map((v1, idx) => [[v1, l2[idx]].join(what)]);
        }
    }
}