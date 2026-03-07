package com.gzq.uiframework.renderer.view.tree

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ViewTreePatchPipelinePlanTest {
    @Test
    fun `skip subtree plan bypasses child reconcile`() {
        assertFalse(ViewTreePatchPipeline.shouldReconcileChildren(NodeBindingPlan.SkipSubtree))
    }

    @Test
    fun `rebind and skip-self plans still reconcile children`() {
        assertTrue(ViewTreePatchPipeline.shouldReconcileChildren(NodeBindingPlan.Rebind))
        assertTrue(ViewTreePatchPipeline.shouldReconcileChildren(NodeBindingPlan.SkipSelfOnly))
        // Patch branch remains "reconcile children" by design and is covered by integration paths.
    }
}
