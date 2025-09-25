class_name ConfirmDialog extends Control

signal confirmed(is_confirmed: bool)

@onready var header_label: Label = %HeaderLabel
@onready var message_label: Label = %MessageLabel

@onready var cacnel_button: Label = %CancelButton
@onready var action_button: Label = %ActionButton
