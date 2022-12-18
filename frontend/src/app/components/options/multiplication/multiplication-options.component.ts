import { Component, EventEmitter, Input, Output } from '@angular/core';
import { initialMultiplicationProperties, MultiplicationProperties } from "../../../../shared/multiplication-properties";
import { COMMA, ENTER } from "@angular/cdk/keycodes";
import * as _ from "lodash";
import { MatChipInputEvent } from "@angular/material/chips";

@Component({
  selector: 'app-multiplication-options',
  templateUrl: './multiplication-options.component.html',
  styleUrls: ['./multiplication-options.component.scss']
})
export class MultiplicationOptionsComponent {
  @Input("properties") properties: MultiplicationProperties = { ...initialMultiplicationProperties };
  @Output("propertiesChange") private _propertiesEmitter = new EventEmitter<MultiplicationProperties>();

  addOnBlur = true;
  readonly separatorKeysCodes = [ENTER, COMMA] as const;

  constructor() {
  }

  get maxProduct() {
    return this.properties.maxProduct;
  }

  set maxProduct(maxProduct: number) {
    const fixedMultiplicand = maxProduct / 10;
    let fixedMultiplicands = this.properties.fixedMultiplicands;
    let transgression = this.properties.transgression;
    if (maxProduct < 100) {
      fixedMultiplicands = [fixedMultiplicand];
    } else if (this.properties.maxProduct < 100) {
      fixedMultiplicands = [];
    }
    if (maxProduct < 100 && transgression > -1) {
      transgression = -1;
    } else if (maxProduct <= 100 && transgression > 2) {
      transgression = 2;
    } else if (maxProduct <= 1000 && transgression > 6) {
      transgression = 6;
    } else if (maxProduct > 10000) {
      transgression = -1;
    }
    this._propertiesEmitter.emit({
      ...this.properties,
      maxProduct: maxProduct,
      exclusions: _.remove(this.properties.exclusions, value => value !== fixedMultiplicand),
      fixedMultiplicands: fixedMultiplicands,
      transgression: transgression
    });
  }

  get exclusions() {
    return this.properties.exclusions;
  }

  removeExclusion(exclusion: number) {
    this._propertiesEmitter.emit({
      ...this.properties,
      exclusions: _.remove(this.properties.exclusions, value => value !== exclusion)
    })
  }

  addExclusion(event: MatChipInputEvent) {
    const value = (event.value || '').trim();

    if (value) {
      this._propertiesEmitter.emit({
        ...this.properties,
        exclusions: _.concat(this.properties.exclusions, parseInt(value)),
        fixedMultiplicands: _.remove(this.properties.fixedMultiplicands, v => v !== parseInt(value))
      })
    }

    event.chipInput!.clear();
  }

  get fixedMultiplicands() {
    return this.properties.fixedMultiplicands;
  }

  removeFixedMultiplicand(fixedMultiplicand: number) {
    this._propertiesEmitter.emit({
      ...this.properties,
      fixedMultiplicands: _.remove(this.properties.fixedMultiplicands, value => value !== fixedMultiplicand)
    })
  }

  addFixedMultiplicand(event: MatChipInputEvent) {
    const value = (event.value || '').trim();

    if (value) {
      this._propertiesEmitter.emit({
        ...this.properties,
        exclusions: _.remove(this.properties.exclusions, v => v !== parseInt(value)),
        fixedMultiplicands: _.concat(this.properties.fixedMultiplicands, parseInt(value))
      })
    }

    event.chipInput!.clear();
  }

  get transgression() {
    return this.properties.transgression;
  }

  set transgression(transgression: number) {
    this._propertiesEmitter.emit({
      ...this.properties,
      transgression: transgression
    })
  }
}
