package de.upb.crypto.incentive.basketserver.model.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RedeemRequest {
    UUID basketId;
    String redeemRequest;
    int value;
}
