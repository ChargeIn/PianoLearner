class_name ConfirmDialog extends Control

signal confirmed(is_confirmed: bool)

@onready var header_label: Label = %HeaderLabel
@onready var message_label: Label = %MessageLabel

@onready var cacnel_button: Button = %CancelButton
@onready var action_button: Button = %ActionButton

var is_open: bool = false

func _ready() -> void:
	set_process_unhandled_key_input(false)
	action_button.pressed.connect(_on_action_pressed)
	cacnel_button.pressed.connect(_on_cancel_pressed)
	hide()
	
func _unhandled_key_input(event: InputEvent) -> void:
	if event.is_action_pressed("ui_cancel"):
		cancel()
		
func _close_modal(is_confirmed: bool) -> void:
	set_process_unhandled_input(false)
	confirmed.emit(is_confirmed);
	set_deferred('is_open', false)
			
		
func _on_action_pressed() -> void:
	pass
	
func _on_cancel_pressed() -> void:
	pass
