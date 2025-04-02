# Component Lifecycle Documentation

## Overview

The lifecycle of a **Component** in the context of the given system governs how a component transitions through important states and reacts to various events such as initialization, enabling, updates, and destruction. The class design ensures that all these transitions are handled systematically, making the component robust and providing appropriate hooks for developers to add custom behavior at each stage of the lifecycle.

This document outlines the different stages in the lifecycle of a `Component`, the sequence of transitions, and the methods associated with each stage.

---

## Component Lifecycle Stages

The `Component` class manages its lifecycle through the following sequential stages:

### 1. **Addition to Component Manager**
Before a component can function, it must be associated with a `ComponentManager`. This step sets up the foundational data necessary to manage the lifecycle of the component.

- **Method Triggered**: `onAdd(ComponentManager componentManager)`
- **Details**:
    - The `ComponentManager` instance is assigned to the component.
    - An `UpdateTask` is created to handle updates asynchronously if required by the component mode, target, and cache configuration.
    - A log message is generated to indicate the addition of the component.

---

### 2. **Initialization**
Once added, the component is initialized to prepare it for further updates and operations.

- **Method Triggered**: `onInitialize()`
- **Details**:
    - This step is controlled in the `UPDATE_FUNCTION`, ensuring initialization happens only once, regardless of multiple update invocations.
    - A log message is generated to indicate successful initialization.

---

### 3. **Enabling**
A component can be "enabled" to signal that it is ready to participate in updates and other operations.

- **Method Triggered**: `onEnable()`
- **Conditions**:
    - Triggered when the `enabled` flag transitions from `false` to `true`, which is monitored inside the `UPDATE_FUNCTION`.
- **Details**:
    - The `isEnabled` state is updated to reflect that the component is now active.
    - Once enabled, the component will start receiving update calls (if there are no other blocking conditions).
    - A log message notes this transition.

---

### 4. **Updating**
When the component is enabled and active, it enters the repetitive update phase to perform its primary operations.

- **Method Triggered**: `onUpdate()`
- **Conditions**:
    - The component must be marked as `enabled` and `isEnabled` must be `true`.
- **Details**:
    - This is the state where the core functionality of the component is executed on a recurring basis.
    - The update is orchestrated through the `UpdateTask` system, which utilizes an `Async` scheduler if the component operates in asynchronous mode.
    - A log message is produced every time the component is updated.

---

### 5. **Disabling**
A component may need to be temporarily disabled to halt updates while still retaining its relationship with the `ComponentManager`.

- **Method Triggered**: `onDisable()`
- **Conditions**:
    - Triggered when the `enabled` flag transitions from `true` to `false`.
- **Details**:
    - When disabled, the component's `isEnabled` flag is set to `false` to indicate that the component is no longer active.
    - A log message is recorded to indicate the state change.

---

### 6. **Destruction**
Components that are no longer required or must be shut down (e.g., due to an engine shutdown) are destroyed.

- **Method Triggered**: `onDestroy()`
- **Conditions**:
    - Triggered when the engine's shutdown reason is detected, or the component is permanently removed.
    - Happens after disabling, if the component is active.
- **Details**:
    - The `initialized` state is reset to `false`, indicating the component has been properly destroyed and cleaned up.
    - A log message is generated.

---

### 7. **Removal from Component Manager**
Once a component is no longer needed, it can be disassociated from the `ComponentManager`.

- **Method Triggered**: `onRemoval()`
- **Details**:
    - Ensures the `ComponentManager` reference is cleared and cleans up any remaining tasks or resources associated with the component.
    - A log message is recorded to indicate successful removal.

---

## Component Lifecycle Timing

The lifecycle of a `Component` is event-driven, depending on various triggers and states. However, a typical lifecycle flow might follow this pattern:

1. **Addition**: The component is added to the `ComponentManager` (`onAdd`).
2. **Initialization**: The component is initialized (`onInitialize`).
3. **Enable/Disable Cycle**:
    - Transition to enabled state (`onEnable`).
    - Periodic updates while enabled (`onUpdate`).
    - Transition to disabled state when required (`onDisable`).
4. **Destruction**: Permanent teardown and cleanup (`onDestroy`).
5. **Removal**: Completely removed from the `ComponentManager` (`onRemoval`).

---

## Component Lifecycle Example Workflow

The lifecycle is handled primarily within the `UPDATE_FUNCTION`, which ensures that:
- The component is initialized (`onInitialize`) only once throughout its lifecycle.
- Transitioning between enabled and disabled states is managed properly with calls to `onEnable` and `onDisable`.
- Destruction and cleanup occur only when required, ensuring no ghost operations are executed.

---

## Key Considerations
- **Threading and Asynchronous Behavior**:  
  The lifecycle asynchronously schedules updates and ensures smooth execution even in a multi-threaded or background processing environment using `UpdateTask`.

- **State Management**:  
  The `Component` class keeps its state private (e.g., `enabled`, `isEnabled`, `initialized`) and enforces state integrity through controlled transitions.

- **Extensibility**:  
  Developers can extend the component's lifecycle by overriding lifecycle hooks (`onAdd`, `onInitialize`, `onEnable`, etc.) to provide custom behavior during these stages.

- **Logging**:  
  Each stage is accompanied by trace-level logs for debugging and monitoring the component's lifecycle transitions.

---

By embracing a structured lifecycle, the `Component` class ensures predictable operation, easy debugging, and extensibility, making it a core part of the system's architecture.