/*
 * Copyright (c) Florian Plesker
 * florian.plesker@web.de
 */

package com.flop.pianolerner.data

import java.util.UUID

open class GattAction

class GattReadAction(
    val service: UUID,
    val characteristic: UUID,
    val callback: (value: String) -> Unit
) :
    GattAction()