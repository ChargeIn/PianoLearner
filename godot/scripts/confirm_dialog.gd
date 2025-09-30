class_name ConfirmDialog extends Control

signal confirmed(is_confirmed: bool)

@onready var header_label: Label = %HeaderLabel
@onready var message_label: Label = %MessageLabel

@onready var cacnel_button: Button = %CancelButton
@onready var action_button: Button = %ActionButton

var is_open: bool = false

func _ready() -> void:
	set_process_unhandled_key_input(false)
	action_button.pressed.connect() 
